package fr.cirad.web.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GalaxyClientController {

	private static final Logger LOG = LogManager.getLogger(GalaxyClientController.class);

	static final public String mainPageURL = "/index.do";
	static final public String testURL = "/test.json";

	@GetMapping(mainPageURL)
	protected ModelAndView mainPage(HttpSession session) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		mav.addObject("toto", "hello");
		return mav;
	}

	@PostMapping(testURL)
	protected @ResponseBody Map<String, Object> test(@RequestBody Map<String, Object> body) throws Exception
	{	    
		System.out.println(body);
		return body;
	}
	
	public static String determinePublicHostName(HttpServletRequest request) throws SocketException, UnknownHostException {
		int nPort = request.getServerPort();
		String sHostName = request.getHeader("X-Forwarded-Server"); // in case the app is running behind a proxy
		if (sHostName == null)
			sHostName = request.getServerName();

		// see if we can get this from the referer
		String sReferer = request.getHeader("referer");
		if (sReferer != null) {
			int nPos = sReferer.indexOf("://" + sHostName + request.getContextPath() + "/");
			if (nPos != -1) {
				sHostName = sReferer.substring(0, nPos) + "://" + sHostName;
				LOG.debug("From referer header, determinePublicHostName is returning " + sHostName);
				return sHostName;
			}
		}

		if ("localhost".equalsIgnoreCase(sHostName) || "127.0.0.1".equals(sHostName)) // we need a *real* address for remote applications to be able to reach us
			sHostName = tryAndFindVisibleIp(request);
		sHostName = "http" + (request.isSecure() ? "s" : "") + "://" + sHostName + (nPort != 80 ? ":" + nPort : "");
		LOG.debug("After scanning network interfaces, determinePublicHostName is returning " + sHostName);
		return sHostName;
	}

	private static String tryAndFindVisibleIp(HttpServletRequest request) throws SocketException, UnknownHostException {
		String sHostName = null;
		HashMap<InetAddress, String> inetAddressesWithInterfaceNames = getInetAddressesWithInterfaceNames();
        for (InetAddress addr : inetAddressesWithInterfaceNames.keySet()) {
            LOG.debug("address found for local machine: " + addr /*+ " / " + addr.isAnyLocalAddress() + " / " + addr.isLinkLocalAddress() + " / " + addr.isLoopbackAddress() + " / " + addr.isMCLinkLocal() + " / " + addr.isMCNodeLocal() + " / " + addr.isMCOrgLocal() + " / " + addr.isMCSiteLocal() + " / " + addr.isMulticastAddress() + " / " + addr.isSiteLocalAddress() + " / " + addr.isMCGlobal()*/);
            String hostAddress = addr.getHostAddress().replaceAll("/", "");
            if (!hostAddress.startsWith("127.0.") && hostAddress.split("\\.").length >= 4)
            {
            	sHostName = hostAddress;
            	if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress() && !addr.isSiteLocalAddress() && !inetAddressesWithInterfaceNames.get(addr).toLowerCase().startsWith("wl"))
           			break;	// otherwise we will keep searching in case we find an ethernet network
            }
        }
        if (sHostName == null)
        	throw new UnknownHostException("Unable to convert local address to visible IP");
        return sHostName;
    }

	public static HashMap<InetAddress, String> getInetAddressesWithInterfaceNames() throws SocketException {
		HashMap<InetAddress, String> result = new HashMap<>();
		Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
        for (; niEnum.hasMoreElements();)
        {
            NetworkInterface ni = niEnum.nextElement();
            Enumeration<InetAddress> a = ni.getInetAddresses();
            for (; a.hasMoreElements();)
            	result.put(a.nextElement(), ni.getDisplayName());
        }
		return result;
	}
}
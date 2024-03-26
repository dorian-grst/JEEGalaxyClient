package fr.cirad.web.controller;

import java.net.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

import fr.cirad.test.Blend4jUtils;

@Controller
public class GalaxyClientController {

    private static final Logger LOG = LogManager.getLogger(GalaxyClientController.class);
    static final public String ROOT = "galaxyClient";
    static final public String MAIN_PAGE_URL = "/index.do";
    static final public String HISTORIES_URL = "/histories.do";
    //    static final public String DATASETS_URL = "/datasets.do"
    static final public String UPLOAD_URL = "/upload.do";
    static final public String WORKFLOW_URL = "/workflow.do";

    //    static final public String UPLOADED_URL = "/uploaded.do";
    static final public String INVOKE_URL = "/invoke.do";
    static final public String TEST_URL = "/test.json";

    @Autowired
    private ServletContext servletContext;

    @GetMapping("/" + ROOT + MAIN_PAGE_URL)
    protected ModelAndView mainPage(@RequestParam("filesURLs") List<String> filesURLs, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("filesURLs", filesURLs);
        return mav;
    }

    @GetMapping("/" + ROOT + HISTORIES_URL)
    protected ModelAndView historiesPage(@RequestParam("filesURLs") List<String> filesURLs, @RequestParam("galaxyUrl") String galaxyUrl, @RequestParam("apiKey") String apiKey, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        Blend4jUtils Blend4jUtils = new Blend4jUtils(galaxyUrl, apiKey, false);
        try {
            if (!Objects.equals(Blend4jUtils.userExist(), "")) {
                List<History> histories = Blend4jUtils.getHistoriesList();
                mav.addObject("histories", histories);
                mav.addObject("userName", Blend4jUtils.userExist());
                mav.addObject("galaxyUrl", galaxyUrl);
            } else {
                LOG.error("Wrong API key or URL.");
                mav.addObject("error", "Wrong API key or URL.");
            }
        } catch (Exception e) {
            LOG.error("An error occurred while retrieving histories.", e);
            mav.addObject("error", "An error occurred while retrieving histories.");
        }
        return mav;
    }

//    @GetMapping("/" + ROOT + DATASETS_URL)
//    protected ModelAndView datasetsPage(@RequestParam("historyId") String historyId, HttpSession session) {
//        ModelAndView mav = new ModelAndView();
//        Blend4jUtils Blend4jUtils = (Blend4jUtils) session.getAttribute("Blend4jUtils");
//        try {
//            mav.setViewName("datasets");
//            List<HistoryContents> datasets = Blend4jUtils.getNonDeletedDatasetsList(historyId);
//            mav.addObject("datasets", datasets);
//            mav.addObject("historyId", historyId);
//        } catch (Exception e) {
//            mav.setViewName("index");
//            mav.addObject("error", "An error occurred while retrieving datasets.");
//        }
//        return mav;
//    }

    @GetMapping("/" + ROOT + UPLOAD_URL)
    protected ModelAndView uploadPage(@RequestParam("filesURLs") List<String> filesURLs, @RequestParam("galaxyUrl") String galaxyUrl, @RequestParam("apiKey") String apiKey, @RequestParam("historyId") String historyId, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("historyId", historyId);
        return mav;
    }

//    @GetMapping(UPLOADED_URL)
//    public ModelAndView uploadedPage(@RequestParam("historyId") String historyId, @RequestParam("fileList") List<String> fileList, HttpSession session) throws Exception {
//        Blend4jUtils Blend4jUtils = (Blend4jUtils) session.getAttribute("Blend4jUtils");
//        ModelAndView mav = new ModelAndView("uploaded");
//        mav.addObject("historyId", historyId);
//        mav.addObject("fileList", fileList);
//        Blend4jUtils.uploadDatasetsToHistory(historyId, fileList);
//        return mav;
//    }

    @GetMapping("/" + ROOT + WORKFLOW_URL)
    protected ModelAndView workflowPage(@RequestParam("filesURLs") List<String> filesURLs, @RequestParam("galaxyUrl") String galaxyUrl, @RequestParam("apiKey") String apiKey, @RequestParam("historyId") String historyId, HttpSession session) throws Exception {
        Blend4jUtils Blend4jUtils = new Blend4jUtils(galaxyUrl, apiKey, false);
        ModelAndView mav = new ModelAndView();
        try {
            Map<String, String> fileExtensions = Blend4jUtils.parseExtension(filesURLs);
            List<Workflow> compatibleWorkflows = Blend4jUtils.getWorkflowCompatibleWithFiles(fileExtensions);
            mav.addObject("historyId", historyId);
            mav.addObject("compatibleWorkflows", compatibleWorkflows);
            mav.addObject("filesURLs", filesURLs);
        } catch (Exception e) {
            mav.setViewName(ROOT + MAIN_PAGE_URL);
            LOG.error("An error occurred while processing datasets.", e);
            mav.addObject("error", "An error occurred while processing datasets.");
        }
        return mav;
    }

    @GetMapping("/" + ROOT + INVOKE_URL)
    protected ModelAndView invokePage(@RequestParam("filesURLs") List<String> filesURLs, @RequestParam("galaxyUrl") String galaxyUrl, @RequestParam("apiKey") String apiKey, @RequestParam("historyId") String historyId, @RequestParam("workflowId") String workflowId, HttpSession session) {
        Blend4jUtils Blend4jUtils = new Blend4jUtils(galaxyUrl, apiKey, false);
        ModelAndView mav = new ModelAndView();

        try {
            Blend4jUtils.uploadDatasetsToHistory(historyId, filesURLs);
            mav.addObject("historyId", historyId);
            mav.addObject("workflowId", workflowId);
        } catch (Exception e) {
            mav.addObject("error", e.getMessage());
        }
        return mav;
    }

    @PostMapping("/" + ROOT + TEST_URL)
    protected @ResponseBody Map<String, Object> test(@RequestBody Map<String, Object> body) throws Exception {
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
            if (!hostAddress.startsWith("127.0.") && hostAddress.split("\\.").length >= 4) {
                sHostName = hostAddress;
                if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress() && !addr.isSiteLocalAddress() && !inetAddressesWithInterfaceNames.get(addr).toLowerCase().startsWith("wl"))
                    break;    // otherwise we will keep searching in case we find an ethernet network
            }
        }
        if (sHostName == null)
            throw new UnknownHostException("Unable to convert local address to visible IP");
        return sHostName;
    }

    public static HashMap<InetAddress, String> getInetAddressesWithInterfaceNames() throws SocketException {
        HashMap<InetAddress, String> result = new HashMap<>();
        Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
        for (; niEnum.hasMoreElements(); ) {
            NetworkInterface ni = niEnum.nextElement();
            Enumeration<InetAddress> a = ni.getInetAddresses();
            for (; a.hasMoreElements(); )
                result.put(a.nextElement(), ni.getDisplayName());
        }
        return result;
    }

}
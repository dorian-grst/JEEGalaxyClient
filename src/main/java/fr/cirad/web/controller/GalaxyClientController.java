package fr.cirad.web.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

import fr.cirad.test.Blend4jTest;

@Controller
public class GalaxyClientController {

    private static final Logger LOG = LogManager.getLogger(GalaxyClientController.class);

    static final public String MAIN_PAGE_URL = "/index.do";
    static final public String HISTORIES_URL = "/histories.do";
    //    static final public String DATASETS_URL = "/datasets.do"
    static final public String UPLOAD_URL = "/upload.do";
    static final public String WORKFLOW_URL = "/workflow.do";

    //    static final public String UPLOADED_URL = "/uploaded.do";
    static final public String INVOKE_URL = "/invoke.do";
    static final public String TEST_URL = "/test.json";

    @GetMapping(MAIN_PAGE_URL)
    protected ModelAndView mainPage() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("toto", "hello");
        return mav;
    }

    @GetMapping(HISTORIES_URL)
    protected ModelAndView historiesPage(@RequestParam("galaxyUrl") String galaxyUrl, @RequestParam("apiKey") String apiKey, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        Blend4jTest blend4jTest = new Blend4jTest(galaxyUrl, apiKey, false);
        try {
            if (!Objects.equals(blend4jTest.userExist(), "")) {
                mav.setViewName("histories");
                List<History> histories = blend4jTest.getHistoriesList();
                mav.addObject("histories", histories);
                mav.addObject("userName", blend4jTest.userExist());
                session.setAttribute("blend4jTest", blend4jTest);
            } else {
                mav.setViewName("index");
                LOG.error("Wrong API key or URL.");
                mav.addObject("error", "Wrong API key or URL.");
            }
        } catch (Exception e) {
            mav.setViewName("index");
            LOG.error("An error occurred while retrieving histories.", e);
            mav.addObject("error", "An error occurred while retrieving histories.");
        }
        return mav;
    }

//    @GetMapping(DATASETS_URL)
//    protected ModelAndView datasetsPage(@RequestParam("historyId") String historyId, HttpSession session) {
//        ModelAndView mav = new ModelAndView();
//        Blend4jTest blend4jTest = (Blend4jTest) session.getAttribute("blend4jTest");
//        try {
//            mav.setViewName("datasets");
//            List<HistoryContents> datasets = blend4jTest.getNonDeletedDatasetsList(historyId);
//            mav.addObject("datasets", datasets);
//            mav.addObject("historyId", historyId);
//        } catch (Exception e) {
//            mav.setViewName("index");
//            mav.addObject("error", "An error occurred while retrieving datasets.");
//        }
//        return mav;
//    }

    @GetMapping(UPLOAD_URL)
    protected ModelAndView uploadPage(@RequestParam("historyId") String historyId) {
        ModelAndView mav = new ModelAndView("upload");
        mav.addObject("historyId", historyId);
        return mav;
    }

//    @GetMapping(UPLOADED_URL)
//    public ModelAndView uploadedPage(@RequestParam("historyId") String historyId, @RequestParam("fileList") List<String> fileList, HttpSession session) throws Exception {
//        Blend4jTest blend4jTest = (Blend4jTest) session.getAttribute("blend4jTest");
//        ModelAndView mav = new ModelAndView("uploaded");
//        mav.addObject("historyId", historyId);
//        mav.addObject("fileList", fileList);
//        blend4jTest.uploadDatasetsToHistory(historyId, fileList);
//        return mav;
//    }

    @GetMapping(WORKFLOW_URL)
    protected ModelAndView workflowPage(@RequestParam("historyId") String historyId, @RequestParam("fileList") List<String> fileList, HttpSession session) throws Exception {
        Blend4jTest blend4jTest = (Blend4jTest) session.getAttribute("blend4jTest");
        ModelAndView mav = new ModelAndView();
        try {
            mav.setViewName("workflow");
            Map<String, String> fileExtensions = blend4jTest.parseExtension(fileList);
            List<Workflow> compatibleWorkflows = blend4jTest.getWorkflowCompatibleWithFiles(fileExtensions);
            mav.addObject("historyId", historyId);
            mav.addObject("fileList", fileList);
            mav.addObject("galaxyUrl", blend4jTest.getGalaxyUrl());
            mav.addObject("apiKey", blend4jTest.getApiKey());
            mav.addObject("compatibleWorkflows", compatibleWorkflows);
        } catch (Exception e) {
            mav.setViewName("index");
            LOG.error("An error occurred while processing datasets.", e);
            mav.addObject("error", "An error occurred while processing datasets.");
        }

        return mav;
    }

    @GetMapping(INVOKE_URL)
    protected ModelAndView invokePage(@RequestParam("historyId") String historyId, @RequestParam("workflowId") String workflowId, @RequestParam("fileList") List<String> fileList, HttpSession session) throws Exception {
        Blend4jTest blend4jTest = (Blend4jTest) session.getAttribute("blend4jTest");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("invoke");
        blend4jTest.uploadDatasetsToHistory(historyId, fileList);
        mav.addObject("historyId", historyId);
        mav.addObject("workflowId", workflowId);
        mav.addObject("galaxyUrl", blend4jTest.getGalaxyUrl());
        mav.addObject("apiKey", blend4jTest.getApiKey());
        return mav;
    }

    @PostMapping(TEST_URL)
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
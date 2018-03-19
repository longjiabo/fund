package com.longjiabo.fund.controller.ihome;

import com.longjiabo.fund.model.ihome.IpInfo;
import com.longjiabo.fund.repository.IpInfoRepository;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/ihome")
public class IHomeController {

    private static final Logger log = LoggerFactory
            .getLogger(IHomeController.class);
    @Autowired
    private IpInfoRepository ipInfoRepository;

    @RequestMapping("/ip/redir")
    public void redir(String xcode, HttpServletResponse response) {
        if (StringUtils.isEmpty(xcode) || !"haohome".equals(xcode)) {
            writeMsg(response, "Under construction...");
        } else {

            response.setHeader("Location", getRealHost());
            response.setStatus(HttpStatus.SC_SEE_OTHER);
        }
    }

    private String getRealHost() {
        IpInfo ip = ipInfoRepository.findCurrentIp();
        return "http://" + ip.getIp() + ":9393/";
    }

    @RequestMapping("/ip/qr")
    public String qr(String xcode, HttpServletRequest request,
                     HttpServletResponse response) {
        if (StringUtils.isEmpty(xcode) || !"haohome".equals(xcode)) {
            writeMsg(response, "Under construction...");
            return null;
        } else {
            request.setAttribute("url", getRealHost());
            return "qr";
        }
    }

    @RequestMapping("/ip/update")
    public void update(String xcode, HttpServletRequest request,
                       HttpServletResponse response) {
        if (StringUtils.isEmpty(xcode) || !"haohome".equals(xcode)) {
            writeMsg(response, "Under construction...");
        } else {
            IpInfo ip = ipInfoRepository.findCurrentIp();
            if (getClientIp(request).equals(ip.getIp())) {
                writeMsg(response, "IP unchanged. No update.");
            } else {
                ip = new IpInfo();
                ip.setIp(getClientIp(request));
                ip.setCreatedOn(new Date());
                ipInfoRepository.save(ip);
                writeMsg(response, "Updated to " + ip.getIp());
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!StringUtils.isEmpty(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;

    }

    private void writeMsg(HttpServletResponse response, String msg) {
        try {
            response.getWriter().write(msg);
            response.getWriter().close();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}

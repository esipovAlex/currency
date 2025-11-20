package org.example.servlets;

import org.example.service.DbService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private DbService service;

    @Override
    public void init() throws ServletException {
        super.init();
        service = new DbService();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"PATCH".equals(req.getMethod())) {
            super.service(req, resp);
            return;
        }
        doPatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String json = service.findByCodes(pathInfo);
        resp.setStatus(HttpServletResponse.SC_OK);
        out(resp, json);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String rawForm = getRawForm(req);
        String json = service.update(pathInfo, rawForm);
        resp.setStatus(HttpServletResponse.SC_OK);
        out(resp, json);
    }

    private void out(HttpServletResponse resp, String json) throws IOException {
        try (OutputStream output = resp.getOutputStream()) {
            output.write(json.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }

    private String getRawForm(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}

package org.example.servlets;

import org.example.service.DbService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    private DbService service;

    @Override
    public void init() throws ServletException {
        super.init();
        service = new DbService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String json = service.getCurrency(pathInfo);
        resp.setStatus(HttpServletResponse.SC_OK);
        out(resp, json);
    }

    private void out(HttpServletResponse resp, String json) throws IOException {
        try (OutputStream output = resp.getOutputStream()) {
            output.write(json.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }
}

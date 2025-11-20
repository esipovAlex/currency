package org.example.servlets;

import com.google.gson.Gson;
import org.example.model.response.AllCurrencyDto;
import org.example.store.AllCurrency;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AllCurrencyServlet", value = "/allcurrencies")
public class AllCurrencyServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<AllCurrencyDto> currencyDtos = AllCurrency.dtos();
        String json = gson.toJson(currencyDtos);
        resp.getWriter().write(json);
    }
}
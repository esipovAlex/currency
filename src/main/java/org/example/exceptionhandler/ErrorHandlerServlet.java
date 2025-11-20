package org.example.exceptionhandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ErrorHandlerServlet", urlPatterns = "/error")
public class ErrorHandlerServlet extends HttpServlet {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";
        if (throwable != null) {
            message = throwable.getMessage();
            String errClassName = throwable.getClass().getSimpleName();
            statusCode = switch (errClassName) {
                case "CodeCurrenciesException",
                     "RateParameterException",
                     "PathVariableException",
                     "FormException",
                     "FieldFormException" ->  HttpServletResponse.SC_BAD_REQUEST;
                case "CurrencyNotFoundException",
                     "CodeCurrencyAbsentException" ->  HttpServletResponse.SC_NOT_FOUND;
                case "CurrencyAlreadyExistsException" ->  HttpServletResponse.SC_CONFLICT;
                default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            };
        }
        resp.setStatus(statusCode);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        String json = GSON.toJson(errorResponse);
        out(resp, json);
    }

    private void out(HttpServletResponse resp, String json) throws IOException {
        try (OutputStream output = resp.getOutputStream()) {
            output.write(json.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }
}

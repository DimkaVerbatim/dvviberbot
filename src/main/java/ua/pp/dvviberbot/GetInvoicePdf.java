package ua.pp.dvviberbot;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "GetInvoicePdf")
public class GetInvoicePdf extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName;
        String requestURI = URLDecoder.decode(request.getRequestURI(), "UTF-8");
        String lc = "";
        String usl = "";
        List<String> listServices = new ArrayList<>();
        Pattern parrentUsl = Pattern.compile("cepdf/(.*?)/");
        Matcher matcherUsl = parrentUsl.matcher(requestURI);
        listServices.add("ГВП");
        listServices.add("ЦО");

        if (matcherUsl.find())
        {
            if (listServices.contains(matcherUsl.group(1)))
                usl = matcherUsl.group(1);
        }

        Pattern parrentLc = Pattern.compile(usl+"/(.*?)\\.pdf");
        Matcher matcherLc = parrentLc.matcher(requestURI);
        if (matcherLc.find())
        {
            if (matcherLc.group(1).matches("^[0-9]{15}$"))
                lc = matcherLc.group(1);
        }

        if (lc.equals("")){
            throw new ServletException(
                    "Invalid or non-existent file parameter in url");
        }
        fileName = lc + ".pdf";

        ExternalDataService externalDataService = new ExternalDataService();
        String s = externalDataService.getLCAbonentMonthsInfoBitek(lc, usl);

        OutputStream outSteam = null;

        try {

            outSteam = response.getOutputStream();

            //set response headers
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename="
                    + fileName);

            InvoiceFacade facade = new InvoiceFacade();
            AbonentMonths abonentMonths = new AbonentMonths(s);

            facade.writeAsPdf(abonentMonths,outSteam);

        } catch (IOException ioe) {
            throw new ServletException(ioe.getMessage());
        } finally {
            if (outSteam != null)
                outSteam.close();
        }

    }
}

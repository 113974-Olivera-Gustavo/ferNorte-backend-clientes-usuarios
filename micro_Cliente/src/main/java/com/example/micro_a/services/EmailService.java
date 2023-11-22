package com.example.micro_a.services;


import com.example.micro_a.configs.EmailConfig;
import com.example.micro_a.dtos.catalogo.ProductoRequest;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    public void sendProductCatalog(String email, List<ProductoRequest> productos) {

        Properties properties = EmailConfig.getProperties();

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Catálogo de Productos en Oferta");

            // Construye el contenido HTML del catálogo
            String htmlContent = buildProductCatalogHtml(productos);
            message.setContent(htmlContent, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo electrónico: " + e.getMessage());
        }
    }

    public void sendProductCatalogFilter(String email, List<ProductoRequest> productos, Double cantidadPuntos,String nombreCliente) {

        Properties properties = EmailConfig.getProperties();
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Catálogo de Productos en Oferta");

            // Construye el contenido HTML del catálogo
            String htmlContent = buildProductCatalogFilterHtml(productos, cantidadPuntos,nombreCliente);
            message.setContent(htmlContent, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo electrónico: " + e.getMessage());
        }
    }
    private String buildProductCatalogFilterHtml(List<ProductoRequest> productos, Double puntosCliente, String nombreCliente) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append("<html>");
        htmlBuilder.append("<head>");
        htmlBuilder.append("<meta charset=\"UTF-8\">");
        htmlBuilder.append("<title>Catálogo de Productos en Oferta</title>");
        htmlBuilder.append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj\" crossorigin=\"anonymous\">");
        htmlBuilder.append("<style>");
        htmlBuilder.append("body { background-color: #f0f0f0; padding: 20px; }");
        htmlBuilder.append(".product-container { margin-bottom: 30px; padding: 20px; background-color: #ffffff; border-radius: 8px; }");
        htmlBuilder.append("</style>");
        htmlBuilder.append("</head>");
        htmlBuilder.append("<body>");
        // Saludo personalizado con el nombre del cliente
        htmlBuilder.append("<h1>").append("Hola, ").append(nombreCliente).append("! Aprovecha nuestros productos en Oferta!</h1>");
        htmlBuilder.append("<h2>").append("Contas con: ").append(puntosCliente).append(" Puntos.");

        htmlBuilder.append("<table class=\"table\">");

        for (int i = 0; i < productos.size(); i++) {

            ProductoRequest producto = productos.get(i);
            if (tieneSuficientesPuntos(producto, puntosCliente)){
                // Inicia una nueva fila para cada tercer producto
                if (i % 3 == 0) {
                    htmlBuilder.append("<tr>");
                }

                htmlBuilder.append("<td class=\"product-container\">");
                htmlBuilder.append("<div>");
                htmlBuilder.append("<h2>").append(productos.get(i).getNombre()).append("</h2>");
                htmlBuilder.append("<p>").append(productos.get(i).getDescripcion()).append("</p>");
                htmlBuilder.append("<p>").append("Precio Oferta: $").append(productos.get(i).getOfertas().get(0).getPrecio_oferta()).append("</p>");
                htmlBuilder.append("<p>").append(productos.get(i).getOfertas().get(0).getPuntos()).append(" Puntos.").append("</p>");

                if (productos.get(i).getImageURL() != null && !productos.get(i).getImageURL().isEmpty()) {
                    htmlBuilder.append("<img src=\"").append(productos.get(i).getImageURL()).append("\" alt=\"").append(productos.get(i).getNombre())
                            .append("\" style=\"max-width: 150px; height: auto;\">");
                }
                htmlBuilder.append("</div>");
                htmlBuilder.append("</td>");

                // Cierra la fila después del tercer producto o al final de la lista
                if ((i + 1) % 3 == 0 || i == productos.size() - 1) {
                    htmlBuilder.append("</tr>");
                }
            }
        }

        htmlBuilder.append("</table>");
        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");

        System.out.println("HTML Content: " + htmlBuilder.toString());
        return htmlBuilder.toString();
    }

    private String buildProductCatalogHtml(List<ProductoRequest> productos) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append("<html>");
        htmlBuilder.append("<head>");
        htmlBuilder.append("<meta charset=\"UTF-8\">");
        htmlBuilder.append("<title>Catálogo de Productos en Oferta</title>");
        htmlBuilder.append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj\" crossorigin=\"anonymous\">");
        htmlBuilder.append("<style>");
        htmlBuilder.append("body { background-color: #f0f0f0; padding: 20px; }");
        htmlBuilder.append(".product-container { margin-bottom: 30px; padding: 20px; background-color: #ffffff; border-radius: 8px; }");
        htmlBuilder.append("</style>");
        htmlBuilder.append("</head>");
        htmlBuilder.append("<body>");
        htmlBuilder.append("<h1>Aprovecha nuestros productos en Oferta!</h1>");

        htmlBuilder.append("<table class=\"table\">");

        for (int i = 0; i < productos.size(); i++) {
            // Inicia una nueva fila para cada tercer producto
            if (i % 3 == 0) {
                htmlBuilder.append("<tr>");
            }

            htmlBuilder.append("<td class=\"product-container\">");
            htmlBuilder.append("<div>");
            htmlBuilder.append("<h2>").append(productos.get(i).getNombre()).append("</h2>");
            htmlBuilder.append("<p>").append(productos.get(i).getDescripcion()).append("</p>");
            htmlBuilder.append("<p>").append("Precio Oferta: $").append(productos.get(i).getOfertas().get(0).getPrecio_oferta()).append("</p>");
            htmlBuilder.append("<p>").append("Puntos a descontar: ").append(productos.get(i).getOfertas().get(0).getPuntos()).append("</p>");

            if (productos.get(i).getImageURL() != null && !productos.get(i).getImageURL().isEmpty()) {
                htmlBuilder.append("<img src=\"").append(productos.get(i).getImageURL()).append("\" alt=\"").append(productos.get(i).getNombre())
                        .append("\" style=\"max-width: 150px; height: auto;\">");
            }
            htmlBuilder.append("</div>");
            htmlBuilder.append("</td>");

            // Cierra la fila después del tercer producto o al final de la lista
            if ((i + 1) % 3 == 0 || i == productos.size() - 1) {
                htmlBuilder.append("</tr>");
            }
        }

        htmlBuilder.append("</table>");
        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");

        System.out.println("HTML Content: " + htmlBuilder.toString());
        return htmlBuilder.toString();
    }

    private boolean tieneSuficientesPuntos(ProductoRequest producto, Double puntosCliente) {
        return producto.getOfertas().stream()
                .anyMatch(oferta -> oferta.getActivo() && puntosCliente >= oferta.getPuntos());
    }
}

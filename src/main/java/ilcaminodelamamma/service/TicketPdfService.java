package ilcaminodelamamma.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import ilcaminodelamamma.model.Comanda;
import ilcaminodelamamma.model.DetalleComanda;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketPdfService {

    private static final String TICKETS_FOLDER = "src/main/resources/img/tickets";
    private static final String RESTAURANT_NAME = "Il Camino Della Mamma";
    private static final String RESTAURANT_ADDRESS = "Calle Betis, 45, 41010 Sevilla";
    private static final String RESTAURANT_PHONE = "TEL: 954 123 456";
    private static final double IVA_RATE = 0.21; // 21% IVA

    // Tamaño de ticket: 210mm ancho (aprox. 595 puntos) por altura variable
    private static final float TICKET_WIDTH = 210; // mm convertido a puntos
    private static final float TICKET_HEIGHT = 841.89f; // A4 height, se ajustará al contenido

    /**
     * Genera un ticket PDF para una comanda
     * @param comanda La comanda para la cual generar el ticket
     * @return La ruta del archivo PDF generado
     * @throws Exception Si hay algún error en la generación
     */
    public String generarTicket(Comanda comanda) throws Exception {
        // Crear carpeta de tickets si no existe
        File ticketsDir = new File(TICKETS_FOLDER);
        if (!ticketsDir.exists()) {
            ticketsDir.mkdirs();
        }

        // Generar nombre del archivo
        String fileName = generarNombreArchivo(comanda);
        String filePath = TICKETS_FOLDER + File.separator + fileName;

        // Tamaño personalizado para ticket (210mm x altura ajustable)
        PageSize ticketSize = new PageSize(TICKET_WIDTH * 2.83465f, TICKET_HEIGHT);

        // Crear el documento PDF con try-with-resources
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, ticketSize)) {

            // Márgenes pequeños para ticket
            document.setMargins(10, 20, 10, 20);

            // Configurar fuentes
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.COURIER);

            // === ENCABEZADO ===
            document.add(new Paragraph(RESTAURANT_NAME)
                    .setFont(fontBold)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2));

            document.add(new Paragraph(RESTAURANT_ADDRESS)
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(1));

            document.add(new Paragraph(RESTAURANT_PHONE)
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8));

            // Línea separadora
            document.add(new Paragraph("----------------------------------------")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // === INFORMACIÓN DE LA COMANDA ===
            document.add(new Paragraph("Nº Comanda: " + comanda.getId_comanda())
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setMarginBottom(2));

            document.add(new Paragraph("Mesa: " + comanda.getMesa().getId_mesa())
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setMarginBottom(2));

            document.add(new Paragraph("Camarero: " + comanda.getUsuario().getNombre())
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setMarginBottom(2));

            document.add(new Paragraph("Fecha: " + formatearFecha(comanda.getFecha_hora()))
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setMarginBottom(8));

            // Línea separadora
            document.add(new Paragraph("----------------------------------------")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // === PRODUCTOS ===
            double subtotal = 0.0;

            for (DetalleComanda detalle : comanda.getDetalleComandas()) {
                String nombreProducto = detalle.getReceta().getNombre();
                int cantidad = detalle.getCantidad();
                double precioUnitario = detalle.getPrecio_unitario();
                double totalLinea = cantidad * precioUnitario;
                subtotal += totalLinea;

                // Línea del producto (nombre)
                document.add(new Paragraph(nombreProducto)
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setMarginBottom(1));

                // Línea de cantidad y precio
                String lineaDetalle = String.format("  %.2f x %d", precioUnitario, cantidad);
                String totalStr = String.format("%.2f", totalLinea);
                
                Paragraph detalleParrafo = new Paragraph()
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setMarginBottom(4);
                
                detalleParrafo.add(new Text(lineaDetalle));
                // Espaciado para alinear el total a la derecha
                int espacios = 35 - lineaDetalle.length() - totalStr.length();
                if (espacios > 0) {
                    detalleParrafo.add(new Text(" ".repeat(espacios)));
                }
                detalleParrafo.add(new Text(totalStr));
                
                document.add(detalleParrafo);
            }

            // Línea separadora
            document.add(new Paragraph("----------------------------------------")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // === TOTALES ===
            double iva = subtotal * IVA_RATE;
            double totalSinIva = subtotal;
            double total = subtotal + iva;

            // TOTAL SIN IVA
            Paragraph totalSinIvaP = new Paragraph()
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setMarginBottom(2);
            String labelSinIva = "TOTAL SIN IVA";
            String valorSinIva = String.format("%.2f EUR", totalSinIva);
            totalSinIvaP.add(new Text(labelSinIva));
            int espaciosSinIva = 35 - labelSinIva.length() - valorSinIva.length();
            if (espaciosSinIva > 0) {
                totalSinIvaP.add(new Text(" ".repeat(espaciosSinIva)));
            }
            totalSinIvaP.add(new Text(valorSinIva));
            document.add(totalSinIvaP);

            // IVA
            Paragraph ivaP = new Paragraph()
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setMarginBottom(2);
            String labelIva = String.format("IVA(%d) %.2f%%", (int)(IVA_RATE * 100), IVA_RATE * 100);
            String valorIva = String.format("%.2f EUR", iva);
            ivaP.add(new Text(labelIva));
            int espaciosIva = 35 - labelIva.length() - valorIva.length();
            if (espaciosIva > 0) {
                ivaP.add(new Text(" ".repeat(espaciosIva)));
            }
            ivaP.add(new Text(valorIva));
            document.add(ivaP);

            // Línea separadora
            document.add(new Paragraph("----------------------------------------")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // TOTAL FINAL
            Paragraph totalP = new Paragraph()
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setMarginBottom(8);
            String labelTotal = "TOTAL EUR";
            String valorTotal = String.format("%.2f", total);
            totalP.add(new Text(labelTotal));
            int espaciosTotal = 35 - labelTotal.length() - valorTotal.length();
            if (espaciosTotal > 0) {
                totalP.add(new Text(" ".repeat(espaciosTotal)));
            }
            totalP.add(new Text(valorTotal));
            document.add(totalP);

            // Línea separadora
            document.add(new Paragraph("----------------------------------------")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8));

            // === PIE DE PÁGINA ===
            document.add(new Paragraph("Gracias por su visita!")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(3));

            document.add(new Paragraph("Vuelva pronto")
                    .setFont(fontRegular)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER));

        } // El documento se cierra automáticamente con try-with-resources

        return filePath;
    }

    /**
     * Genera el nombre del archivo PDF basado en la comanda
     */
    private String generarNombreArchivo(Comanda comanda) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fechaHora = ahora.format(formatter);
        
        int numeroMesa = comanda.getMesa().getId_mesa();
        
        return String.format("ticket_mesa_%d_%s.pdf", numeroMesa, fechaHora);
    }

    /**
     * Formatea la fecha de la comanda
     */
    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formatter);
    }
}

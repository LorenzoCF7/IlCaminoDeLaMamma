package ilcaminodelamamma.util;

import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Receta;
import javafx.scene.image.Image;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Clase responsable de cargar imágenes desde los recursos y asignarlas a las recetas
 */
public class ImagenLoader {
    
    private final RecetaDAO recetaDAO;
    
    public ImagenLoader() {
        this.recetaDAO = new RecetaDAO();
    }
    
    /**
     * Carga una imagen desde los recursos y la asigna a una receta
     * @param rutaImagen Ruta de la imagen en los recursos (debe empezar con /)
     * @param idReceta ID de la receta
     * @return true si se cargó correctamente
     */
    public boolean cargarImagenReceta(String rutaImagen, Integer idReceta) {
        try {
            System.out.println("📷 Cargando imagen para receta ID: " + idReceta);
            System.out.println("   Ruta: " + rutaImagen);
            
            // Asegurarse de que la ruta empiece con /
            if (!rutaImagen.startsWith("/")) {
                rutaImagen = "/" + rutaImagen;
            }
            
            // Cargar imagen desde recursos
            InputStream inputStream = getClass().getResourceAsStream(rutaImagen);
            
            if (inputStream == null) {
                System.err.println("❌ Error: No se pudo encontrar la imagen en recursos");
                System.err.println("   Ruta intentada: " + rutaImagen);
                return false;
            }
            
            // Leer los bytes de la imagen
            byte[] imagenBytes = inputStream.readAllBytes();
            inputStream.close();
            
            System.out.println("   Tamaño original: " + imagenBytes.length + " bytes");
            
            // Comprimir la imagen
            Image img = new Image(new java.io.ByteArrayInputStream(imagenBytes));
            byte[] imagenComprimida = comprimirImagen(img);
            
            if (imagenComprimida == null || imagenComprimida.length == 0) {
                System.err.println("❌ Error: La imagen comprimida está vacía");
                return false;
            }
            
            System.out.println("   Tamaño comprimido: " + imagenComprimida.length + " bytes");
            
            // Obtener la receta y asignar la imagen
            Receta receta = recetaDAO.findById(idReceta);
            if (receta == null) {
                System.err.println("❌ Error: No se encontró la receta con ID " + idReceta);
                return false;
            }
            
            receta.setImagen(imagenComprimida);
            recetaDAO.update(receta);
            
            System.out.println("✅ Imagen asignada correctamente a receta: " + receta.getNombre());
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando imagen para receta ID " + idReceta + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Carga imágenes de ejemplo para las recetas
     */
    public void cargarImagenesEjemplo() {
        System.out.println("\n📸 Cargando imágenes de ejemplo...");
        
        // Mapeo de ID de receta -> ruta de imagen en recursos
        cargarImagenReceta("/img/entrantes/bruschetta-clasica.jpg", 1);
        cargarImagenReceta("/img/entrantes/ensalada-caprese-receta-original-italiana.jpg", 2);
        cargarImagenReceta("/img/entrantes/carpaccio-de-ternera.jpg", 3);
        cargarImagenReceta("/img/entrantes/quesos-italianos.jpg", 4);
        cargarImagenReceta("/img/entrantes/sopas-minestrone.jpg", 5);
        cargarImagenReceta("/img/entrantes/calamares-fritos.jpg", 6);
        cargarImagenReceta("/img/entrantes/Provolone-al-horno-1-scaled.jpg", 7);
        cargarImagenReceta("/img/entrantes/tartar-de-salmon-y-aguacate.jpg", 8);
        cargarImagenReceta("/img/entrantes/full.Mixed_Antipasto.jpg", 9);
        cargarImagenReceta("/img/pasta/espaguetis-a-la-carbonara.jpg", 10);
        cargarImagenReceta("/img/pasta/Penne-all-Arrabbiata_EXPS_TOHD24_277252_KristinaVanni_6.jpg", 11);
        cargarImagenReceta("/img/pasta/tagliatelle-al-pesto.jpg", 12);
        cargarImagenReceta("/img/pasta/lasagna-boloñesa.jpg", 13);
        cargarImagenReceta("/img/pasta/ravioli-ricotta-espinacas.jpg", 14);
        cargarImagenReceta("/img/pasta/Noquis-a-la-sorrentina_650x433_wm.jpg", 15);
        cargarImagenReceta("/img/pasta/one-pot-alfredo-recipe.jpg", 16);
        cargarImagenReceta("/img/pasta/tortellini_pannaprosciuttopiselli.jpg", 17);
        cargarImagenReceta("/img/pasta/marinara-sauce-18.jpg", 18);
        cargarImagenReceta("/img/pizza/margherita-1-scaled.jpg", 19);
        cargarImagenReceta("/img/pizza/pepperoni.jpg", 20);
        cargarImagenReceta("/img/pizza/pizza-4-quesos.jpg", 21);
        cargarImagenReceta("/img/pizza/hawaiana.jpg", 22);
        cargarImagenReceta("/img/pizza/bbq-pollo.jpg", 23);
        cargarImagenReceta("/img/pizza/pizza-prosciutto-e-funghi-1.jpg", 24);
        cargarImagenReceta("/img/pizza/pizza-vegetariana.jpg", 25);
        cargarImagenReceta("/img/pizza/Pizza-alla-diavola_650x433_wm.jpg", 26);
        cargarImagenReceta("/img/pizza/calzone.jpg", 27);
        cargarImagenReceta("/img/pescados/salmon-en-salsa-de-limon.jpg", 28);
        cargarImagenReceta("/img/pescados/lubina-al-horno-con-patatas.jpg", 29);
        cargarImagenReceta("/img/pescados/bacalao-con-tomate.jpg", 30);
        cargarImagenReceta("/img/pescados/atun_a_la_parrilla_31410_orig.jpg", 31);
        cargarImagenReceta("/img/pescados/merluza-salsa-verde-receta.jpg", 32);
        cargarImagenReceta("/img/pescados/dorada-a-la-espalda-receta.jpg", 33);
        cargarImagenReceta("/img/pescados/pulpo-brasa.jpg", 34);
        cargarImagenReceta("/img/pescados/calamares-tinta-1-scaled.jpg", 35);
        cargarImagenReceta("/img/pescados/fritura-mixata.jpg", 36);
        cargarImagenReceta("/img/carnes/Pollo-empanado-air-fryer.jpg", 37);
        cargarImagenReceta("/img/carnes/churrasco.jpg", 38);
        cargarImagenReceta("/img/carnes/estofado-ternera.jpg", 39);
        cargarImagenReceta("/img/carnes/chuleta.jpg", 40);
        cargarImagenReceta("/img/carnes/albondigas.jpg", 41);
        cargarImagenReceta("/img/carnes/cordero-asado.jpg", 42);
        cargarImagenReceta("/img/carnes/lasana-de-carne-con-verduras.jpg", 43);
        cargarImagenReceta("/img/carnes/costillas-bbq.jpg", 44);
        cargarImagenReceta("/img/carnes/churrasco.jpg", 45);
        cargarImagenReceta("/img/postres/Tiramisu-clasico.jpg", 46);
        cargarImagenReceta("/img/postres/PANACOTTA-CON-FRUTOS-ROJOS.jpg", 47);
        cargarImagenReceta("/img/postres/helado-artesanal.jpg", 48);
        cargarImagenReceta("/img/postres/brownie-con-helado-destacada.jpg", 49);
        cargarImagenReceta("/img/postres/tarta-queso-horno-receta.jpg", 50);
        cargarImagenReceta("/img/postres/coulant-de-chocolate_515_1.jpg", 51);
        cargarImagenReceta("/img/postres/fruta-fresca.jpg", 52);
        cargarImagenReceta("/img/postres/Cannoli-siciliani_1200x800.jpg", 53);
        cargarImagenReceta("/img/postres/gelato-affogato.jpg", 54);
        cargarImagenReceta("/img/menu-infantil/MENU-INFANTIL.jpeg", 55);
        cargarImagenReceta("/img/menu-infantil/pasta.jpg", 56);
        cargarImagenReceta("/img/vino/rioja-vega-crianza.jpg", 57);
        cargarImagenReceta("/img/vino/albariñoriasbaixas.jpg", 58);
        cargarImagenReceta("/img/vino/chianti-docg.jpg", 59);
        cargarImagenReceta("/img/vino/ribera-duero-crianza.jpg", 60);
        cargarImagenReceta("/img/vino/valdeorras-o-luar-do-sil-godello-sobre-lias-75-cl.jpg", 61);
        cargarImagenReceta("/img/vino/barolo-joven.jpg", 62);
        cargarImagenReceta("/img/vino/rivera-duero.jpg", 63);
        cargarImagenReceta("/img/vino/chablis-premier-cru-montmains-simonnet-febvre.jpg", 64);
        cargarImagenReceta("/img/vino/brunello.jpg", 65);
        
        System.out.println("✅ Carga de imágenes de ejemplo completada");
    }
    
    /**
     * Comprime una imagen a formato JPEG
     */
    private byte[] comprimirImagen(Image imagen) {
        try {
            int width = (int) imagen.getWidth();
            int height = (int) imagen.getHeight();
            
            // Calcular nueva dimensión si es muy grande
            int maxWidth = 800;
            int maxHeight = 600;
            
            if (width > maxWidth || height > maxHeight) {
                double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
                width = (int) (width * ratio);
                height = (int) (height * ratio);
            }
            
            // Convertir a BufferedImage
            java.awt.image.BufferedImage bufferedOriginal = javafx.embed.swing.SwingFXUtils.fromFXImage(imagen, null);
            
            // Redimensionar si es necesario
            java.awt.image.BufferedImage bufferedFinal;
            if (width != imagen.getWidth() || height != imagen.getHeight()) {
                bufferedFinal = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2d = bufferedFinal.createGraphics();
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(bufferedOriginal, 0, 0, width, height, null);
                g2d.dispose();
            } else {
                bufferedFinal = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2d = bufferedFinal.createGraphics();
                g2d.drawImage(bufferedOriginal, 0, 0, null);
                g2d.dispose();
            }
            
            // Comprimir como JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageWriter jpgWriter = javax.imageio.ImageIO.getImageWritersByFormatName("jpg").next();
            javax.imageio.ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(0.85f);
            
            jpgWriter.setOutput(javax.imageio.ImageIO.createImageOutputStream(baos));
            jpgWriter.write(null, new javax.imageio.IIOImage(bufferedFinal, null, null), jpgWriteParam);
            jpgWriter.dispose();
            
            byte[] comprimido = baos.toByteArray();
            baos.close();
            
            return comprimido;
            
        } catch (Exception e) {
            System.err.println("❌ Error comprimiendo imagen: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

package ar.gft;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public class SignTest {
    final static File RESOURCES_FOLDER = new File("src/test/resources/ar/gft/");

    @Test
    public void testSignDocument() throws IOException, NoSuchAlgorithmException {
        // Instance of the PdfManager
        PdfManager manager = new PdfManager();

        // File input
        File input = new File(RESOURCES_FOLDER, "sample.pdf");

        // File output
        File output = new File(RESOURCES_FOLDER, "Signed.pdf");

        // Create a new file for the signed document to be saved
        OutputStream fileOutputStream = new FileOutputStream(output);

        // Load the document
        PDDocument document = Loader.loadPDF(input);

        // Prepare to sign
        OpenPdf openPdf = manager.prepareToSign(document, fileOutputStream);

        // Print the hash to sign
        System.out.println("Base64 to sign: " + openPdf.getHashToSign());

        // Attach the sign
        String base64Hash = null;
        manager.attachSign(openPdf, base64Hash);

        // Close the output stream
        fileOutputStream.close();
    }
}

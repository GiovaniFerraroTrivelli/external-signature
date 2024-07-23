package ar.gft;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;

public class PdfManager {

    private static final String LOCATION = "Santa Fe";
    private static final String REASON = "Aprobado";

    public OpenPdf prepareToSign(PDDocument pdDocument, OutputStream resultingFileOutputStream)
            throws IOException, NoSuchAlgorithmException {
        // se valida que el documento sea firmable
        SignatureUtils.validatePermissions(pdDocument);

        // se crea el contenedor de la firma
        var signature = createSignature(LOCATION, REASON);

        // se agrega la información de la firma al documento
        pdDocument.addSignature(signature);

        // se guarda el documento a la espera de la firma externa
        var externalSigningSupport =
                pdDocument.saveIncrementalForExternalSigning(resultingFileOutputStream);

        // se obtiene la parte del documento a firmar
        var content = externalSigningSupport.getContent();

        // se obtiene el algoritmo de hashing
        var mdigest = MessageDigest.getInstance("SHA-256");

        // se obtiene el hash para firmar
        var hashBytes = mdigest.digest(content.readAllBytes());

        // se codifica a Base 64
        var base64Hash = Base64.getEncoder().encodeToString(hashBytes);

        // se devuelve el objeto con todos los datos a almacenar
        return new OpenPdf(base64Hash, base64Hash, externalSigningSupport, pdDocument);
    }

    public void attachSign(OpenPdf doc, String signedHash) throws IOException {
        doc.getExternalSigningSupport().setSignature(Base64.getDecoder().decode(signedHash));
        doc.getPdDocument().close();
    }

    private PDSignature createSignature(String location, String reason) {
        var signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setLocation(location);
        signature.setReason(reason);
        signature.setSignDate(Calendar.getInstance());

        return signature;
    }
}

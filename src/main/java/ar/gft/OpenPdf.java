package ar.gft;

import lombok.Data;
import lombok.NonNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;

@Data
public class OpenPdf {

    @NonNull
    private String id;

    @NonNull
    private String hashToSign;

    @NonNull
    private ExternalSigningSupport externalSigningSupport;

    @NonNull
    private PDDocument pdDocument;
}

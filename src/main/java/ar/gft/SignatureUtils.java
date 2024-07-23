package ar.gft;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;

class SignatureUtils {

    static void validatePermissions(PDDocument pdDocument) {
        if (1 == getMDPPermission(pdDocument)) {
            throw new IllegalStateException(
                    "No changes to the document are permitted due to DocMDP transform parameters dictionary");
        }
    }

    /**
     * Get the access permissions granted for this document in the DocMDP transform parameters
     * dictionary. Details are described in the table "Entries in the DocMDP transform parameters
     * dictionary" in the PDF specification.
     *
     * @param doc document.
     * @return the permission value. 0 means no DocMDP transform parameters dictionary exists. Other
     * return values are 1, 2 or 3. 2 is also returned if the DocMDP transform parameters
     * dictionary is found but did not contain a /P entry, or if the value is outside the valid
     * range.
     */
    static int getMDPPermission(PDDocument doc) {
        COSDictionary permsDict =
                doc.getDocumentCatalog().getCOSObject().getCOSDictionary(COSName.PERMS);
        if (permsDict != null) {
            COSDictionary signatureDict = permsDict.getCOSDictionary(COSName.DOCMDP);
            if (signatureDict != null) {
                COSArray refArray = signatureDict.getCOSArray(COSName.REFERENCE);
                if (refArray != null) {
                    for (int i = 0; i < refArray.size(); ++i) {
                        COSBase base = refArray.getObject(i);
                        if (base instanceof COSDictionary) {
                            COSDictionary sigRefDict = (COSDictionary) base;
                            if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject(COSName.TRANSFORM_METHOD))) {
                                base = sigRefDict.getDictionaryObject(COSName.TRANSFORM_PARAMS);
                                if (base instanceof COSDictionary) {
                                    COSDictionary transformDict = (COSDictionary) base;
                                    int accessPermissions = transformDict.getInt(COSName.P, 2);
                                    if (accessPermissions < 1 || accessPermissions > 3) {
                                        accessPermissions = 2;
                                    }
                                    return accessPermissions;
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    private SignatureUtils() {
    }
}

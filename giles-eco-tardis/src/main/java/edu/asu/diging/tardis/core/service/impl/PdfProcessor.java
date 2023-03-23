package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.IImageExtractionRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;

public class PdfProcessor extends PDFStreamEngine {
    
    private String dirFolder;
    private List<String> imageFilenames;
    
    public PdfProcessor(IFileStorageManager storageManager, ICompletedStorageRequest request) {
        this.imageFilenames = new ArrayList<>();
        dirFolder = storageManager.getAndCreateStoragePath(request.getRequestId(), request.getDocumentId(), null);
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        // TODO Auto-generated method stub
        super.processPage(page);
    }
    
    public void resetFilenames() {
        imageFilenames = new ArrayList<String>();
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands)
            throws IOException {
        if( operator.getName().equals("Do") ) {
            COSName objectName = (COSName) operands.get( 0 );
            PDXObject xobject = getResources().getXObject( objectName );
            if( xobject instanceof PDImageXObject)
            {
                PDImageXObject image = (PDImageXObject)xobject;
 
                // same image to local
                BufferedImage bImage = image.getImage();
                String filename = "image_"+UUID.randomUUID().toString().hashCode() +".png";
                ImageIO.write(bImage,"PNG",new File(dirFolder + File.separator + filename));
                imageFilenames.add(filename); 
            }
            else if(xobject instanceof PDFormXObject)
            {
                PDFormXObject form = (PDFormXObject)xobject;
                showForm(form);
            }
        }
        else
        {
            super.processOperator( operator, operands);
        }
        super.processOperator(operator, operands);
    }

    public List<String> getImageFilenames() {
        return this.imageFilenames;
    }
}

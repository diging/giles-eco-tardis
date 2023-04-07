package edu.asu.diging.tardis.core.service;

import java.io.IOException;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IImageExtractionManager {

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.gilesecosystem.cepheus.service.pdf.impl.
     * IImageExtractionManager #extractImages(edu.asu.diging.gilesecosystem.requests
     * .IImageExtractionRequest)
     */
    void extractImages(ICompletedStorageRequest request);

}
package edu.asu.diging.tardis.core.service;


import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IImageExtractionManager {

    /**

    Extracts images from a completed storage request.
    @param request The completed storage request containing the image to extract images from.
    */
    void extractImages(ICompletedStorageRequest request);

}
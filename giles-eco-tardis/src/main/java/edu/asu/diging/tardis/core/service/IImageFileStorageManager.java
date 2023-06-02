package edu.asu.diging.tardis.core.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IImageFileStorageManager {
    public String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException;
}

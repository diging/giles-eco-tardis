package edu.asu.diging.tardis.core.service;

import java.io.IOException;

import edu.asu.diging.tardis.core.exception.InnogenScriptRunnerException;

public interface IInnogenScriptRunner {
    /**
    Runs the Innogen script on the specified image for the given page number.
    @param imagePath The path of the image file to process.
    @param pageNr The page number of the image within the document.
     * @throws InnogenScriptRunnerException 
    */
    public void runInnogenScript(String imagePath, int pageNr) throws InnogenScriptRunnerException;
}

package edu.asu.diging.tardis.core.service;

import java.io.IOException;

public interface IInnogenScriptRunner {
    public void runInnogenScript(String imagePath) throws IOException, InterruptedException;
}

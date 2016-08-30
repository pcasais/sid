package com.damosais.sid.webapp;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;

/**
 * This class contains the references to the icons and resources of the project
 * 
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class GraphicResources {
    /* LOGOS */
    public static final String IMG_PATH = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/img/";
    public static final FileResource SECURITY_LOGO = new FileResource(new File(IMG_PATH + "logo.png"));
    public static final FileResource ADD_ICON = new FileResource(new File(IMG_PATH + "add.png"));
    public static final FileResource DELETE_ICON = new FileResource(new File(IMG_PATH + "delete.png"));
    public static final FileResource EDIT_ICON = new FileResource(new File(IMG_PATH + "edit.png"));
    public static final FileResource DEFACEMENT_ICON = new FileResource(new File(IMG_PATH + "defacement.png"));
    public static final FileResource SELECT_ICON = new FileResource(new File(IMG_PATH + "select.png"));
    public static final FileResource ATLAS_ICON = new FileResource(new File(IMG_PATH + "atlas.png"));
    public static final FileResource SAVE_ICON = new FileResource(new File(IMG_PATH + "save.png"));
    public static final FileResource UPLOAD_ICON = new FileResource(new File(IMG_PATH + "upload.png"));
    
    private GraphicResources() {
    }
}
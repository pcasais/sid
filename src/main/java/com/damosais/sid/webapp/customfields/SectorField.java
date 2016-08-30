package com.damosais.sid.webapp.customfields;

import com.damosais.sid.database.beans.Sector;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Tree;

/**
 * This class generates a tree to handle the selection of the sector
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public class SectorField extends CustomField<Sector> {
    private static final long serialVersionUID = 9079903004364672243L;
    private final Tree sectorTree;
    private Sector selected;
    
    /**
     * Creates a new field to select the sector
     */
    public SectorField() {
        sectorTree = new Tree();
        sectorTree.setSelectable(true);
        sectorTree.setMultiSelect(false);
        sectorTree.setNullSelectionAllowed(true);
        sectorTree.setImmediate(true);
        setCaption("Sector");
        for (final Sector sector : Sector.values()) {
            sectorTree.addItem(sector);
        }
        for (final Sector sector : Sector.values()) {
            if (sector.getParent() != null) {
                sectorTree.setParent(sector, sector.getParent());
            }
        }
        sectorTree.addValueChangeListener(event -> {
            if (event.getProperty() != null && event.getProperty().getValue() != null) {
                if (selected != (Sector) event.getProperty().getValue()) {
                    selected = (Sector) event.getProperty().getValue();
                    super.setValue(selected);
                }
            } else {
                selected = null;
            }
        });
    }
    
    @Override
    public Sector getInternalValue() {
        return selected;
    }
    
    @Override
    public Class<? extends Sector> getType() {
        return Sector.class;
    }
    
    @Override
    public Sector getValue() {
        return selected;
    }

    @Override
    protected com.vaadin.ui.Component initContent() {
        return sectorTree;
    }
    
    @Override
    public void setInternalValue(Sector sector) {
        if (sector != null && sector.getParent() != null && !sectorTree.isExpanded(sector.getParent())) {
            sectorTree.expandItem(sector.getParent());
        }
        sectorTree.select(sector);
        super.setInternalValue(sector);
    }
    
    @Override
    public void setValue(Sector sector) {
        sectorTree.select(sector);
        super.setValue(sector);
    }
}
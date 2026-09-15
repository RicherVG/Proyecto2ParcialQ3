/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;

/**
 *
 * @author andre
 */
public class ParrafoConWrap extends ParagraphView {
    public ParrafoConWrap(Element elem){
        super(elem);
    }
    
    
    protected SizeRequirements  calculateMinorAxisRequirementes(int axis, SizeRequirements  r){
     SizeRequirements req = super.calculateMinorAxisRequirements(axis, r);
     req.minimum = 0;
     req.preferred = 0;
     return req;
    }
    
    
}

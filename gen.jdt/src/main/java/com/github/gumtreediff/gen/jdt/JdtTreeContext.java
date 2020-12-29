package com.github.gumtreediff.gen.jdt;

import com.github.gumtreediff.tree.TreeContext;
import org.eclipse.jdt.core.dom.ASTNode;

public class JdtTreeContext extends TreeContext {
    private ASTNode rootNode;

    public JdtTreeContext(TreeContext ctxt, ASTNode rootNode) {
        super(ctxt);
        this.rootNode = rootNode;
    }

    public ASTNode getRootNode() {
        return this.rootNode;
    }
}

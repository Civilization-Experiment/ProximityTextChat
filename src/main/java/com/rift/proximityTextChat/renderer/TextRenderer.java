package com.rift.proximityTextChat.renderer;

import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.Set;

public class TextRenderer implements NodeRenderer {
    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of();
    }

    @Override
    public void render(Node node) {

    }
}

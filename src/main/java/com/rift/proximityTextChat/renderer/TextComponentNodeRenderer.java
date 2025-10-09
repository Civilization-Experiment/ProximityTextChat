package com.rift.proximityTextChat.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

public class TextComponentNodeRenderer extends AbstractVisitor implements NodeRenderer {
    private final Logger logger = LoggerFactory.getLogger(TextComponentNodeRenderer.class);
    private final Stack<TextDecoration> decorators = new Stack<>();
    private final TextComponent.Builder builder = Component.text();

    public TextComponent toComponent() {
        return builder.build();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(
                Document.class,
                Emphasis.class,
                StrongEmphasis.class,
                Text.class
        );
    }

    @Override
    public void render(Node node) {
        node.accept(this);
    }

    @Override
    public void visit(Emphasis emphasis) {
        decorators.push(TextDecoration.ITALIC);
//        logger.info("Encountered emphasis, stack dump : {}", stackDump());
        visitChildren(emphasis);
        decorators.pop();
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        decorators.push(TextDecoration.BOLD);
//        logger.info("Encountered strong emphasis, stack dump : {}", stackDump());
        visitChildren(strongEmphasis);
        decorators.pop();
    }

    @Override
    public void visit(Text text) {
//        logger.info("Encountered text with content {}, stack dump : {}", text.getLiteral(), stackDump());
        builder.append(Component.text(text.getLiteral()).decorate(decorators.toArray(new TextDecoration[] {})));
    }

    private String stackDump() {
        return Arrays.toString(decorators.toArray());
    }
}

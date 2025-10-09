package com.rift.proximityTextChat.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

public class CoreMinecraftNodeRenderer extends AbstractVisitor implements NodeRenderer {
    private final Logger logger = LoggerFactory.getLogger(CoreMinecraftNodeRenderer.class);
    private final Stack<TextComponent.Builder> components = new Stack<>();

    public TextComponent toComponent() {
        return components.pop().build();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(
                Document.class,
                Image.class,
                Emphasis.class,
                StrongEmphasis.class,
                Text.class
        );
    }

    @Override
    protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            node.accept(this);
            node = next;
        }
    }

    @Override
    public void render(Node node) {
        node.accept(this);
    }

    @Override
    public void visit(Document document) {
        // No rendering itself
        visitChildren(document);
    }

//    @Override
//    public void visit(Link link) {
//        Map<String, String> attrs = new LinkedHashMap<>();
//        String url = link.getDestination();
//
//        if (context.shouldSanitizeUrls()) {
//            url = context.urlSanitizer().sanitizeLinkUrl(url);
//            attrs.put("rel", "nofollow");
//        }
//
//        url = context.encodeUrl(url);
//        attrs.put("href", url);
//        if (link.getTitle() != null) {
//            attrs.put("title", link.getTitle());
//        }
//        html.tag("a", getAttrs(link, "a", attrs));
//        visitChildren(link);
//        html.tag("/a");
//    }

    @Override
    public void visit(Emphasis emphasis) {
        logger.info("Encountered emphasis {}", stack());
        components.peek().append(Component.text().decorate(TextDecoration.ITALIC));
        components.push(Component.text());
        visitChildren(emphasis);
        components.pop();
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        logger.info("Encountered strong emphasis {}", stack());
        components.peek().append(Component.text().decorate(TextDecoration.BOLD));
        components.push(Component.text());
        visitChildren(strongEmphasis);
        components.pop();
    }

    @Override
    public void visit(Text text) {
        logger.info("Encountered text {}, {}", text.getLiteral(), stack());
        components.peek().append(Component.text().content(text.getLiteral()));
    }

    public String stack() {
        return Arrays.toString(components.stream().map(ComponentBuilder::build).map(this::toSimpleStr).toArray());
    }

    public String toSimpleStr(TextComponent t) {
        return t.content() + t.style().decorations();
    }
}

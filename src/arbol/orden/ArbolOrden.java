package arbol.orden;

import arbol.orden.controller.InsertHandler;
import arbol.orden.model.Nodo;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static arbol.orden.controller.NodeCreator.RADIO;
import arbol.orden.controller.RotationController;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ArbolOrden extends Application {

    private static final Nodo<Node> arbol = new Nodo<>(null);
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int V_OFFSET = 10;
    private static final int BASE_X = (WIDTH - RADIO) / 2;
    private static final int BASE_Y = RADIO + RADIO + V_OFFSET;
    private static final int DELTA = 40;
    private Stage primaryStage;
    private static final List<Node> inorden = new ArrayList<>();
    private static final List<Node> postorden = new ArrayList<>();
    private static final List<Node> preorden = new ArrayList<>();
    private final InsertHandler insertHandler = new InsertHandler(arbol, this, inorden, postorden, preorden);
    private final TextField input = createInputTextField();
    private final Label preLabel = new Label("Preorden:");
    private final Label postLabel = new Label("Postorden:");
    private final Label inLabel = new Label("Inorden:");
    private static final Button avlButton = new Button("Rotar");

    private TextField createInputTextField() {
        avlButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Nodo<Integer> rotated = RotationController.rotate(insertHandler.getShadowTree());
                insertHandler.setShadowTree(rotated);
                insertHandler.shadowTreeToNodeTree(rotated, arbol);
                crearTablero();
            }
        });
        avlButton.setLayoutX(WIDTH - 100);
        avlButton.setLayoutY(HEIGHT - 210);
        avlButton.setAlignment(Pos.BASELINE_RIGHT);
        TextField field = new TextField();
        field.setLayoutX(0);
        field.setLayoutY(0);
        field.setOnAction(insertHandler);
        return field;
    }

    public ArbolOrden() {
        inLabel.setLayoutX(20);
        preLabel.setLayoutX(20);
        postLabel.setLayoutX(20);
        inLabel.setLayoutY(450);
        preLabel.setLayoutY(500);
        postLabel.setLayoutY(550);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Orden Arbol");
        crearTablero();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void crearTablero() {
        Group root = pintarInterfaz();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
    }

    private Group pintarInterfaz() {
        Group root = new Group();
        paintLabelsOrders(root);
        root.getChildren().add(input);
        paintIsAVLLabel(root);
        root.getChildren().add(avlButton);
        if (arbol != null && arbol.getValue() != null) {
            paintNodos(root, arbol, BASE_X);
        }
        return root;
    }

    private void paintNodos(
            final Group root,
            final Nodo<Node> nodo,
            final int x) {

        int nodeY = calcularLevel(nodo);
        int nodeX = x;
        nodo.getValue().setLayoutX(nodeX);
        nodo.getValue().setLayoutY(nodeY);

        MoveTo moveTo = new MoveTo(nodeX + RADIO, nodeY + RADIO);
        Nodo<Node> left = nodo.getLeft();
        Nodo<Node> right = nodo.getRight();

        if (left != Nodo.EMPTY) {
            root.getChildren().add(
                    paintLine(
                            moveTo,
                            RADIO + x - (1 + left.calcularRightLongitud()) * DELTA,
                            RADIO + calcularLevel(nodo) + BASE_Y));
            paintNodos(root, left, x - (1 + left.calcularRightLongitud()) * DELTA);
        }

        if (right != Nodo.EMPTY) {
            root.getChildren().add(
                    paintLine(
                            moveTo,
                            RADIO + x + (1 + right.calcularLeftLongitud()) * DELTA,
                            RADIO + calcularLevel(nodo) + BASE_Y));
            paintNodos(root, right, x + (1 + right.calcularLeftLongitud()) * DELTA);
        }
        root.getChildren().add(nodo.getValue());
    }

    private int calcularLevel(Nodo<Node> nodo) {
        return BASE_Y * (nodo.calcularAltura() + 1);
    }

    private Path paintLine(MoveTo moveTo, double x, double y) {
        Path path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add(new LineTo(x, y));
        path.setStrokeWidth(3);
        return path;
    }

    private void paintLabelsOrders(final Group root) {
        root.getChildren().add(inLabel);
        paintOrders(root, 450, inorden);
        root.getChildren().add(preLabel);
        paintOrders(root, 500, preorden);
        root.getChildren().add(postLabel);
        paintOrders(root, 550, postorden);
    }

    private void paintOrders(Group root, int y, List<Node> order) {
        for (int i = 0; i < order.size(); i++) {
            Node current = order.get(i);
            current.setLayoutY(y);
            int x = 100 + i * DELTA;
            current.setLayoutX(x);
            if (i + 1 < order.size()) {
                MoveTo moveTo = new MoveTo(x + RADIO, y + RADIO);

                root.getChildren().add(paintLine(moveTo, x + DELTA, y + RADIO));
            }
            root.getChildren().add(current);
        }
    }

    private void paintIsAVLLabel(Group root) {
        boolean esAVL = arbol.esAVL();

        String message = esAVL ? "Es AVL" : "No es AVL";
        Label label = new Label(message);
        label.setLayoutX(WIDTH - 200);
        label.setLayoutY(HEIGHT - 200);
        label.setAlignment(Pos.BOTTOM_RIGHT);
        label.setTextFill(esAVL ? Color.GREEN : Color.RED);
        if (esAVL) {
            avlButton.setDisable(true);
        } else {
            avlButton.setDisable(false);
        }

        root.getChildren().add(label);
    }
}

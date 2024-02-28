package modules.individual;

public class Leg {
    public Module topPart;
    public Module bottomPart;

    public Leg(Module tModule, Module bModule) {
        this.topPart = tModule;
        this.bottomPart = bModule;
    }

    @Override
    public String toString() {
        return String.format("TopModule: %d, BottomModule: %d", topPart.id, bottomPart.id);
    }
}

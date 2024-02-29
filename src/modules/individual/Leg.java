package modules.individual;

public class Leg {
    public Module topPart;
    public Module middlePart;
    public Module bottomPart;

    public Leg(Module tModule, Module bModule, Module mModule) {
        this.topPart = tModule;
        this.middlePart = mModule;
        this.bottomPart = bModule;
    }

    @Override
    public String toString() {
        return String.format("TopModule: %d, MiddleModule: %d, BottomModule: %d", topPart.id, middlePart.id, bottomPart.id);
    }
}

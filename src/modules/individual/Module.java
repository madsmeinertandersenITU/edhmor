package modules.individual;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Module {
    public int id;
    public int type;
    public List<Module> connectedModules;
    public double phase; // Phase of the module

    public Module(int id, int type) {
        this.id = id;
        this.type = type;
        this.connectedModules = new ArrayList<>();
        this.phase = 0; // Initialize with 0 or any other desired initial phase
    }

    public void connectModule(Module module) {
        this.connectedModules.add(module);
        module.connectedModules.add(this);
    }

    @Override
    public String toString() {
        String connections = connectedModules.stream()
                .map(module -> Integer.toString(module.id))
                .collect(Collectors.joining(", "));
        return String.format("Module ID: %d, Type: %d, Connected to: [%s], Phase: %.2f", id, type, connections, phase);
    }
}

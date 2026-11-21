package cn.nukkit.molang.runtime;

/**
 * 宿主主动采样的零参数数值查询。模板共享属性定义，每个运行时独立存储数值。
 * 通过写入入口更新数值时，会同时使依赖宿主状态的查询缓存失效。
 */
public final class MolangNumberProperty {
    private final MolangQueryDefinition definition;

    public MolangNumberProperty(String name, MolangQueryDefinition.Stability stability) {
        definition = MolangQueryDefinition.number(name, 0, MolangQueryDefinition.Effect.READ_ONLY, stability);
    }

    public void register(MolangRuntimeBuilder builder) {
        builder.queryNumber(definition, new Registration(this));
    }

    public Value bind(MolangRuntime runtime) {
        return cell(runtime);
    }

    private Cell cell(MolangRuntime runtime) {
        if (runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner) {
            Cell cell = owner.binding(definition.name()).property();
            if (cell != null && cell.key == this) {
                return cell;
            }
        }
        throw new IllegalArgumentException("The property was not registered for this runtime: " + definition.name());
    }

    /**
     * 单个属性的写入立即生效，并使宿主缓存失效一次。
     */
    public sealed interface Value permits Cell {
        void set(float value);
    }

    /**
     * 同一宿主的一组属性，按绑定顺序一次发布。所有样本均已复制后才使缓存失效。
     */
    public static final class Group {
        private final Cell[] cells;

        private Group(Cell[] cells) {
            this.cells = cells;
        }

        public static Group bind(MolangRuntime runtime, MolangNumberProperty... properties) {
            Cell[] cells = new Cell[properties.length];
            for (int index = 0; index < cells.length; index++) {
                cells[index] = properties[index].cell(runtime);
                for (int previous = 0; previous < index; previous++) {
                    if (cells[index] == cells[previous]) {
                        throw new IllegalArgumentException("Repeated numeric property in sample group");
                    }
                }
            }
            return new Group(cells);
        }

        public void publish(float[] values) {
            if (values.length != cells.length) {
                throw new IllegalArgumentException("Wrong numeric property sample count");
            }
            for (int index = 0; index < cells.length; index++) {
                cells[index].value = values[index];
            }
            if (cells.length != 0) {
                cells[0].owner.invalidateQueries();
            }
        }
    }

    record Registration(MolangNumberProperty key) implements MolangNumberContextQuery {
        Cell bind(MolangRuntimeBuilder.MolangRuntimeImpl owner) {
            return new Cell(owner, key);
        }

        @Override
        public float query(MolangRuntime runtime, Object... arguments) {
            return key.cell(runtime).value;
        }
    }

    static final class Cell implements Value, MolangNumberContextQuery {
        private final MolangRuntimeBuilder.MolangRuntimeImpl owner;
        private final MolangNumberProperty key;
        private float value;

        private Cell(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangNumberProperty key) {
            this.owner = owner;
            this.key = key;
        }

        @Override
        public void set(float value) {
            this.value = value;
            owner.invalidateQueries();
        }

        @Override
        public float query(MolangRuntime runtime, Object... arguments) {
            return value;
        }

        boolean belongsTo(MolangRuntimeBuilder.MolangRuntimeImpl runtime, MolangQueryDefinition signature) {
            return owner == runtime && key.definition.equals(signature);
        }
    }
}

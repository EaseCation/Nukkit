import cn.nukkit.level.util.BitArray;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.BinaryStream;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.io.InputStream;

public class ChunkTest {
    public static void main(String[] args) throws Exception {
        readVanilla();
//        readChunk("chunk_nk.blob");
    }

    public static void readVanilla() throws Exception {
        readChunk("chunk_nocache.blob");
        System.out.println("---");
        readSubChunk("subchunk_-4.blob");
    }

    public static void readChunk(String file) throws Exception {
        try (InputStream in = ChunkTest.class.getClassLoader().getResourceAsStream(file)) {
            BinaryStream stream = new BinaryStream(ByteStreams.toByteArray(in));

            readPalette(stream, 25); // 3D biome

            int borderSize = stream.getVarInt();
            System.out.println("borderSize: "+borderSize);

            System.out.println("feof: "+stream.feof());
        }
    }

    public static void readSubChunk(String file) throws Exception {
        try (InputStream in = ChunkTest.class.getClassLoader().getResourceAsStream(file)) {
            BinaryStream stream = new BinaryStream(ByteStreams.toByteArray(in));
            byte ver = stream.getByteRaw();
            System.out.println("ver: "+ver);
            byte cnt = stream.getByteRaw();
            System.out.println("cnt: "+cnt);
            byte idx = stream.getByteRaw();
            System.out.println("idx: "+idx);

            readPalette(stream, cnt);

            System.out.println("feof: "+stream.feof());
        }
    }

    public static void readPalette(BinaryStream stream, int count) {
        for (int i = 0; i < count; i++) {
            System.out.println("===: "+i);
            byte header = stream.getByteRaw();
            System.out.println("header: "+header);
            int type = header >> 1;
            System.out.println("type: "+type);
            boolean rt = (header & 0b1) == 1;
            System.out.println("rt: "+rt);
            BitArrayVersion v = BitArrayVersion.get(type, true);
            System.out.println("BitArrayVersion: "+v);
            if (v == BitArrayVersion.EMPTY) {
                continue;
            }
            BitArray bitArray = v.createPalette(4096);
            int[] words = bitArray.getWords();
            int size = 1;
            if (v != BitArrayVersion.V0) {
                for (int j = 0; j < words.length; j++) {
                    words[j] = stream.getLInt();
                }
                size = stream.getVarInt();
            }
            Object2IntMap<BlockVector3> map = new Object2IntLinkedOpenHashMap<>();
            for (int x = 0; x < 16; x++) {
                int xi = x << 8;
                for (int z = 0; z < 16; z++) {
                    int xzi = xi | (z << 4);
                    for (int y = 0; y < 16; y++) {
                        map.put(new BlockVector3(x, y, z) {
                            @Override
                            public String toString() {
                                return "(" + this.x + "," + this.y + "," + this.z + ")";
                            }
                        }, bitArray.get(xzi | y));
                    }
                }
            }
            System.out.println("map: "+map);
            IntList palette = new IntArrayList();
            for (int j = 0; j < size; j++) {
                palette.add(stream.getVarInt());
            }
            System.out.println("palette: "+palette);
            // (v486) 234 bedrock, 4484 dirt, 4998 grass, 134 air
        }
    }
}

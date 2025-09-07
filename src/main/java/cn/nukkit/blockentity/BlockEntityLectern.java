package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerTakeLecternBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockEntityLectern extends BlockEntitySpawnable {

    protected ItemBookWritable book;
    private int page;

    public BlockEntityLectern(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.LECTERN;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("book")) {
            Item item = NBTIO.getItemHelper(namedTag.getCompound("book"));
            if (item instanceof ItemBookWritable) {
                book = (ItemBookWritable) item;
            } else {
                namedTag.remove("book");
            }

            page = namedTag.getInt("page");
        }

        level.updateComparatorOutputLevel(this);

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (book == null) {
            namedTag.remove("hasBook");
            namedTag.remove("book");
            namedTag.remove("totalPages");
            namedTag.remove("page");
            return;
        }

        namedTag.putBoolean("hasBook", true);
        namedTag.putCompound("book", NBTIO.putItemHelper(book));
        namedTag.putInt("totalPages", book.getTotalPages());
        namedTag.putInt("page", page);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.LECTERN;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, LECTERN);

        if (book != null) {
            nbt.putBoolean("hasBook", true)
                    .putInt("page", page)
                    .putInt("totalPages", book.getTotalPages())
                    .putCompound("book", NBTIO.putItemHelper(book));
        }

        return nbt;
    }

    public boolean dropBook() {
        return dropBook(null);
    }

    public boolean dropBook(@Nullable Player player) {
        if (book == null) {
            return false;
        }

        if (player != null) {
            PlayerTakeLecternBookEvent event = new PlayerTakeLecternBookEvent(player, this);
            event.call();
            if (event.isCancelled()) {
                return false;
            }
        }

        level.dropItem(upVec(), book.clone());
        setBook(null);
        return true;
    }

    @Nullable
    public ItemBookWritable getBook() {
        return book;
    }

    public boolean setBook(@Nullable ItemBookWritable book) {
        if (Objects.equals(this.book, book)) {
            return false;
        }

        this.book = book;
        if (book == null) {
            page = 0;
        }

        setDirty();
        return true;
    }

    public int getPage() {
        if (book == null) {
            return 0;
        }
        return page;
    }

    public boolean setPage(int page) {
        if (book == null) {
            return false;
        }

        if (this.page == page) {
            return false;
        }

        if (page < 0 || page > book.getTotalPages()) {
            return false;
        }

        this.page = page;
        setDirty();
        return true;
    }

    public int getTotalPages() {
        if (book == null) {
            return 0;
        }
        return book.getTotalPages();
    }
}

package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.Arrays;

@ToString(callSuper = true)
public class SetBookContentsFunction extends LootItemFunction {
    private final String title;
    private final String author;
    private final String[] pages;

    @JsonCreator
    public SetBookContentsFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("title")
            String title,
            @JsonProperty("author")
            String author,
            @JsonProperty("pages")
            String... pages
    ) {
        super(conditions);
        if (title != null && title.length() > ItemBookWritten.MAX_TITLE_LENGTHE) {
            title = title.substring(0, ItemBookWritten.MAX_TITLE_LENGTHE);
        }
        if (author != null && author.length() > ItemBookWritten.MAX_AUTHOR_LENGTHE) {
            author = author.substring(0, ItemBookWritten.MAX_AUTHOR_LENGTHE);
        }
        if (pages != null) {
            if (pages.length >= ItemBookWritten.MAX_PAGES) {
                pages = Arrays.copyOf(pages, ItemBookWritten.MAX_PAGES);
            }
            for (int i = 0; i < pages.length; i++) {
                String page = pages[i];
                if (page.length() > ItemBookWritten.MAX_PAGE_LENGTH) {
                    pages[i] = page.substring(0, ItemBookWritten.MAX_PAGE_LENGTH);
                }
            }
        }
        this.title = title;
        this.author = author;
        this.pages = pages;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (item instanceof ItemBookWritten book) {
            if (title != null && !title.isEmpty()) {
                book.setTitle(title);
            }
            if (author != null && !author.isEmpty()) {
                book.setAuthor(author);
            }
            if (pages != null) {
                for (int i = 0; i < pages.length; i++) {
                    book.insertPage(i, pages[i]);
                }
            }
        }
        return item;
    }
}

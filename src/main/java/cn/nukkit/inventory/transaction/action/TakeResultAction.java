package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerUIComponent;
import cn.nukkit.item.Item;
import lombok.ToString;

@ToString
public class TakeResultAction extends InventoryAction {

    private final PlayerUIComponent ui;

    public TakeResultAction(Item sourceItem, Item targetItem, PlayerUIComponent ui) {
        super(sourceItem, targetItem);
        this.ui = ui;
    }

    @Override
    public boolean isValid(Player source) {
        return ui.canTakeResult(source);
    }

    @Override
    public boolean onPreExecute(Player source) {
        return ui.preTakeResult(source);
    }

    @Override
    public boolean execute(Player source) {
        return ui.onTakeResult(source, sourceItem);
    }

    @Override
    public void onExecuteSuccess(Player source) {
        ui.postTakeResultResolve(source);
    }

    @Override
    public void onExecuteFail(Player source) {
        ui.postTakeResultReject(source);
    }
}

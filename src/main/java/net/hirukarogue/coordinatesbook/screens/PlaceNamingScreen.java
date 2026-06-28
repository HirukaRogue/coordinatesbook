package net.hirukarogue.coordinatesbook.screens;

import net.hirukarogue.coordinatesbook.data.placeNaming.ConfirmRegistrationPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

//essa tela é fundamental para fazer o popup de registro de local
public class PlaceNamingScreen extends Screen {
    private EditBox nameInput;

    public PlaceNamingScreen() {
        super(Component.literal("Nomear Localização"));
    }

    @Override
    protected void init() {
        //Editbox é a caixa de texto onde você colocará o nome do local
        //por padrão é chamado de Meu Local
        this.nameInput = new EditBox(this.font, this.width/2 - 100, this.height / 2 - 20, 200, 20, Component.literal("Nome do local"));
        this.nameInput.setMaxLength(15);
        this.nameInput.setValue("Meu Local");
        this.addRenderableWidget(this.nameInput);

        //Botão confirmar para confirmar a criação do local marcado
        this.addRenderableWidget(Button.builder(Component.literal("Confirmar"), button -> {
            String pName = this.nameInput.getValue().trim();
            if (!pName.isEmpty()) {
                PacketDistributor.sendToServer(new ConfirmRegistrationPayload(pName));
                this.onClose();
            }
        }).bounds(this.width / 2 - 50, this.height / 2 + 10, 100, 20).build());
    }

    //função para renderizar essa tela
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //sempre deixe o super.render primeiro, pois se colocar depois, fica borrado
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        //aqui serve para colocar o título do popup
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    }

    //aqui impede de pausar o jogo quando o pop up aparecer
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

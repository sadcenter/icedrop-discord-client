# Creating verify message
```txt
new MessageBuilder()
    .setEmbed(EmbedFactory.produce()
        .setThumbnail("https://media.discordapp.net/attachments/695176663276978216/888043810221867019/received_332322134635036.png")
        .setTitle("ICEDROP.EU - Weryfikacja")
        .setDescription("Chcesz uzyskać dostęp do wszystkich kanałów oraz stać się pełnoprawnym członkiem serwera? Musisz zweryfikować swoje konto naciskając guzik znajdujący się poniżej!")
        .setFooter(event.getApi().getYourself().getDiscriminatedName(), event.getApi().getYourself().getAvatar()))
    .addComponents(ActionRow.of(Button.primary("verify-confirm", "Zweryfikuj się", "\uD83D\uDCDD")))
    .send(event.getChannel());
```

# Creating ticket message
```txt
new MessageBuilder()
    .setEmbed(EmbedFactory.produce()
        .setThumbnail("https://media.discordapp.net/attachments/695176663276978216/888043810221867019/received_332322134635036.png")
        .setTitle("ICEDROP.EU - Ticket")
        .setDescription("Posiadasz pilną sprawę, którą chcesz przekazać / omówić z administracją? Stwórz ticket i wyjaśnij sytuację.")
        .setFooter(event.getApi().getYourself().getDiscriminatedName(), event.getApi().getYourself().getAvatar()))
    .addComponents(ActionRow.of(Button.primary("ticket-create", "Stwórz ticket", "\uD83D\uDCDD")))
    .send(event.getChannel());
```

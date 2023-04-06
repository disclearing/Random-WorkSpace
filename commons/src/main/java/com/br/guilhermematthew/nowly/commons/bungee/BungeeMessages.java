package com.br.guilhermematthew.nowly.commons.bungee;

import static com.br.guilhermematthew.nowly.commons.CommonsConst.*;

public class BungeeMessages {

    private static final String PREFIXO = PREFIX + "\n\n";

    public static final String

            AGORA_VOCE_TEM_UMA_SESSãO_NO_SERVIDOR = "§f",
            ROOM_STARTED = "§aAtenção! Uma sala de %sala% foi liberada para acesso.",
    //MENSAGEM GLOBAL DE BAN START
    JOGADOR_FOI_BANIDO_TEMPORARIAMENTE = "§cUm jogador foi punido por Uso de trapaças em sua sala.",
            JOGADOR_FOI_BANIDO_PERMANENTEMENTE = "§cUm jogador foi punido por Uso de trapaças em sua sala.",
    //MENSAGEM GLOBAL DE BAN END

    CONTA_JA_MUTADA = "§cEsta conta já está mutada.",
            CONTA_MUTADA = "§aJogador mutado com sucesso!",

    CONTA_NAO_ESTA_MUTADA = "§cEste jogador não está mutado.",
            CONTA_DESMUTADA = "§aJogador desmutado com sucesso!",

    CONTA_DESBANIDA = "§aJogador desbanido com sucesso!",
            CONTA_NAO_ESTA_BANIDA = "§cEsta conta não está banida.",

    MUTADO_PERMANENTEMENTE =
            "\n"
                    + "§cVocê está mutado permanentemente.\n"
                    + "§eVocê pode ser desmutado adquirindo unmute em: " + LOJA + "\n",

    MUTADO_TEMPORARIAMENTE =
            "\n§cVocê está mutado temporariamente.\n"
                    + "§cTempo restante: §e%tempo%\n"
                    + "§eVocê pode ser desmutado adquirindo unmute em: " + LOJA + "\n",

    VOCE_FOI_MUTADO_PERMANENTEMENTE = "§cVocê foi mutado permanentemente!\n"
            + "§cMotivo: %motivo%\n",

    VOCE_FOI_MUTADO_TEMPORARIAMENTE = "§cVocê foi mutado temporariamente!\n"
            + "§cMotivo: %motivo%\n"
            + "§cDuração: %duração%\n",

    JOGADOR_BANIDO_PERMANENTEMENTE = "§aJogador banido permanentemente com sucesso!",
            JOGADOR_BANIDO_TEMPORARIAMENTE = "§aJogador banido temporariamente com sucesso!",
            DONT_BAN_PLAYER_ABOVE_YOU = "§cVocê não pode banir alguém com o cargo superior ao seu.",
            JOGADOR_JA_BANIDO = "§cEste jogador já está banido!",
            JOGADOR_JA_MUTADO = "§cEste jogador já está mutado!",
            DONT_MUTE_ABOVE_YOU = "§cVocê não pode mutar este jogador!",
            JOGADOR_MUTADO_PERMANENTEMENTE = "§aJogador mutado permanentemente com sucesso!",
            JOGADOR_MUTADO_TEMPORARIAMENTE = "§aJogador mutado temporariamente com sucesso!",

    MUTE = "§cUtilize /mute (player) (motivo)",
            TEMPMUTE = "§cUtilize /tempmute (player) (tempo) (motivo)",
            BAN = "§cUtilize /ban (player) (motivo)",
            TEMPBAN = "§cUtilize /tempban (player) (tempo) (motivo)",
            UNBAN = "§cUtilize /unban (player)",
            UNMUTE = "§cUtilize /unmute (player)",

    PERMISSION_ADDED_FOR_PLAYER = "§aVocê adicionou a permissão §f%permissao% para §a%nick%",
            GRUPO_INEXISTENTE = "§cEsse grupo não existe!",
            GRUPO_ALTO = "§cEsse cargo é maior do que o seu.",

    //MESAGENS PARA STAFFERS START
    JOGADOR_FOI_MUTADO_PARA_STAFFER = "§7[O jogador %nick% foi mutado %duração% pelo %mutou%]",
            JOGADOR_FOI_BANIDO_PARA_STAFFER = "§7[O jogador %nick% foi banido %duração% pelo %baniu%]",
            JOGADOR_FOI_DESBANIDO_PARA_STAFFER = "§7[O jogador %nick% foi desbanido por %staffer%]",
            JOGADOR_TEVE_GRUPO_ATUALIZADO_PARA_STAFFER = "§7[O jogador %nick% recebeu o cargo %cargo% §7pelo %setou% com duração %duração%]",
    //MENSAGENS PARA STAFFERS END

    VOCE_RECEBEU_UM_GRUPO = "§aVocê recebeu o rank %cargo% §acom a duração %duração%",
            VOCE_ATUALIZOU_O_CARGO = "§aVocê adicionou o rank %cargo% §ano jogador %nick%",
            VOCE_NAO_PODE_ADICIONAR_UM_CARGO_MAIOR = "§cVocê não pode setar um rank maior que o seu!",
            SEU_GRUPO_EXPIROU = "§cO seu rank %cargo% §cfoi expirado.",

    COMMAND_GROUP_USAGE =
            "§cUtilize: /setgroup <Nick> <Grupo>" +
                    "§cUtilize: /setgroup <Nick> <Grupo> <Duração>",

    //EVENTO START

    EVENTO_MANAGER_USAGE = "§cUtilize o comando /eventomanager (on) ou (off) para liberar a entrada de players.",

    EVENTO_MANAGER_ONLINE = "§aO evento já está liberado.",
            EVENTO_MANAGER_OFFLINE = "§cO evento não está liberado.",
            EVENTO_OFFLINE = "§eNenhum evento está disponivel.",
            VOCE_LIBEROU_A_ENTRADA_NO_EVENTO = "§aVocê liberou a entrada de jogadores.",
            VOCE_LIBEROU_A_ENTRADA_APENAS_PARA_STAFFERS = "§aVocê liberou apenas staffers para entrarem na sala de eventos.",
            EVENTO_LIBERADO = "\n§eO servidor de §b§lEVENTO §e foi liberado!\n\n§aevento.leaguemc.com.br\n\n§7Clique para conectar-se.",

    //EVENTO END

    ALERT = "§cUtilize para anunciar em todos os servidores /alert <mensagem>",
            ALERT_PREFIX = "§4§lALERTA §f",
            COMMAND_GO_USAGE = "§cUtilize: /go <Nick>",
            PLAYER_FINDED = "§eJogador encontrado!\n§eOnline no servidor: §b%servidor%",
            PLAYER_KICKED = "§aJogador(a) expulso(a) com sucesso!",
            DONT_KICK_PLAYER_ABOVE_YOU = "§cVocê não pode expulsar alguém com o grupo §6§lSUPERIOR §cque o seu.",
            KICKING_ALL_PLAYERS = "§aExpulsando todos os jogadores online...",
            KICK_USAGE = "§cUtilize /kick (player) (motivo)",


    CONNECTING = "§aConectando...",
            VOCE_JA_ESTA_NESTE_SERVIDOR = "§cVocê ja está neste servidor.",

    //REPORT START

    AGUARDE_PARA_REPORTAR_NOVAMENTE = "§cAguarde alguns segundos para reportar um jogador novamente.",
            COMMAND_REPORT_USAGE = "§cUtilize: /report <Nick> <Motivo>",
            VOCE_NAO_PODE_SE_REPORTAR = "§cVocê não pode reportar a si mesmo!",
            JOGADOR_REPORTADO_COM_SUCESSO = "§aJogador reportado com sucesso!",
            REPORT_ENABLED = "§aVocê agora receberá as notificações de report.",
            REPORT_DISABLED = "§cVocê agora não receberá as notificações de report.",
            NEW_REPORT = "§cNOVO REPORT: %target% (Reportado pelo: %from%) - Motivo: %cause%",

    //REPORT END

    //COMMAND SEND START

    COMMAND_SEND_USAGE = "\n"
            + "§cUtilize: /send <Nick> <Servidor>\n"
            + "§cUtilize: /send todos <Servidor>\n"
            + "§cUtilize: /send local <Servidor>\n"
            + "§cUtilize: /send <Servidor> <Servidor>\n",

    SERVER_NOT_EXIST = "§cEste servidor não existe!",
            VOCE_NAO_PODE_ENVIAR_PARA_ESTE_SERVIDOR = "§cVocê não pode enviar ninguém para este servidor.",
            VOCE_NAO_PODE_PUXAR_DESTE_SERVIDOR = "§cVocê não pode puxar ninguém deste servidor.",

    SENDED_FOR_OTHER_SERVER = "§aVocê foi redirecionado para outro servidor!",
            TRYING_SEND_PLAYER_FOR_SERVER = "§aTentando enviar o(a) jogador(a) %nick% §apara o servidor: §a%destino%",
            TRYING_SEND_ALL_FOR_SERVER = "§aTentando enviar todos os JOGADORES §aonline para o servidor: §a%destino%",
            TRING_SEND_LOCAL_FOR_SERVER = "§aTentando enviar todos os JOGADORES §aonline no seu servidor para o: §a%destino%",
            TRYING_SEND_SERVER_TO_SERVER = "§aTentando enviar todos os JOGADORES §ado servidor §a%servidor% §apara o §a%destino%",

    //COMMAND SEND STOP

    //STAFFCHAT START

    VOCE_ENTROU_NO_STAFFCHAT = "§aVocê entrou no staffchat.",
            VOCE_SAIU_DO_STAFFCHAT = "§cVocê saiu do staffchat.",
            VOCE_DESATIVOU_O_STAFFCHAT = "§cVocê desativou o StaffChat.",
            VOCE_ATIVOU_O_STAFFCHAT = "§aVocê ativou o StaffChat.",
            STAFFCHAT_JA_ESTA_ATIVADO = "§aO StaffChat já está ativado!",
            STAFFCHAT_JA_ESTA_DESATIVADO = "§cO StaffChat já está desativado!",
            VOCE_ESTA_TENTANDO_FALAR_NO_STAFFCHAT_COM_ELE_DESATIVADO = "§cVocê está no StaffChat, mas não está vendo as mensagens. Ative-as.",
            STAFFCHAT_PREFIX = "§e§l(STAFF)",

    //STAFCHAT END

    CLAN_CHAT_PREFIX = "§e§l(CLAN)",
            STAFFLIST_PREFIX = "§6§lLISTA DE STAFFERS ONLINE",
            STAFFLIST_PLAYER = "§f- %nick% §7: %servidor%",

    SKIN_ATUALIZADA = "§aSua skin foi atualizada com sucesso!",
            ERROR_ON_UPDATE_SKIN = "§cOcorreu um erro ao tentar atualizar sua skin!",
            ERROR_ON_CHANGE_SKIN = "§cOcorreu um erro ao tentar trocar sua skin!",

    VOCE_NAO_TEM_PERMISSãO_PARA_USAR_ESTE_COMANDO = "§cVocê não tem permissão para usar este comando.",

    PREMIUM_MAP_NOT_LOADED = "§fO seu registro não foi verificado, tente entrar novamente.",

    ERROR_ON_LOAD_PREMIUM_MAP = "§cNão foi possivel verificar sua conta, tente entrar novamente.",

    PREMIUM_MAP_DETECT_NICK_CHANGE = "§cDetectamos uma alteração em seu nickname, espere até que os dados sejam transferidos.",

    INVALID_NICKNAME = PREFIX + "\n§cO seu nickname deve ser no mínimo 3, e no máximo 16 caracteres, e não pode conter caracteres especiais.",

    BUNGEECORD_CARREGANDO = "§cO servidor está carregando, aguarde para poder entrar.",
            SERVIDOR_EM_MANUTENÇãO ="§cEste servidor está disponivel somente para membros da equipe.",

    SUA_CONTA_ESTA_SENDO_DESCARREGADA = "§cErro ao carregar sua sessão, tente novamente mais tarde.",

    ERROR_ON_APPLY_SKIN = "§cHouve um erro ao carregar sua skin em nossa API-MINESKIN.",

    ERROR_ON_LOAD_ACCOUNT = "Não foi possivel carregar sua conta, tente novamente mais tarde.",

    ERROR_ON_CONNECT_TO_X_SERVER = "§cNão foi possivel conectar-se a uma sala LB.",

    // PUNIÇŐES

    VOCE_FOI_BANIDO = "§cVocê foi banido do servidor.\n" + "§cPor: %baniu%\n"
            + "§cMotivo: %motivo%\n" + "\n" + "§cSolicite seu appeal no nosso discord: " + DISCORD + "\n"
            + "§cAdquira unban em nossa loja: " + LOJA,

    VOCE_ESTA_PERMANENTEMENTE_BANIDO = "§cVocê está permanentemente banido.\n\n"
            + "§cMotivo: %motivo%\n" + "§cAutor: %baniu%\n\n" + "§cSolicite seu appeal no nosso discord: "
            + DISCORD + "\n" + "§cAdquira unban em nossa loja: " + LOJA,

    VOCE_ESTA_TEMPORARIAMENTE_BANIDO = "§cVocê está temporariamente banido.\n\n"
            + "§cDuração: %tempo%\n\n" + "§cMotivo: %motivo%\n" + "§cAutor: %baniu%\n\n"
            + "§cSolicite seu appeal no nosso discord: " + DISCORD + "\n"
            + "§cAdquira unban em nossa loja: " + LOJA,
    // PUNIÇŐES END;

    JOGADOR_NAO_POSSUI_SESSAO = "§cEste jogador não possuí uma sessão valida no servidor.",
            NAO_POSSUI_SESSAO = "§cO jogador não tem uma sessão válida no servidor.",
            JOGADOR_OFFLINE = "§fJogador offline!",
            TEMPO_INVALIDO = "§cTempo inválido.",
            NAO_TEM_CONTA = "§cO jogador não possuí um registro na rede.",

    VOCE_FOI_EXPULSO = PREFIXO +
            "§cVocê foi expulso.\n"
            + "§cExpulso por: %expulsou%\n"
            + "§cMotivo: %motivo%",

    //clan
    CLAN_NOME_INVALIDO = "§cEste nome é invalido!",
            CLAN_INEXISTENTE = "§cEste clan não existe.",
            CLAN_FULL = "§cEste clan está lotado!",
            CLAN_CRIADO = "§aClan criado com sucesso!",
            VOCE_NAO_ESTA_EM_UM_CLAN = "§cVocê não possui um clan.",
            VOCE_JA_ESTA_EM_UM_CLAN = "§cVocê ja está em um clan.",
            VOCE_SAIU_DO_CHAT_DO_CLAN = "§cVocê saiu do Chat do clan!",
            VOCE_ENTROU_NO_CHAT_DO_CLAN = "§aVocê entrou do Chat do clan!",
            CLAN_NAME_INVALIDO = "§cVocê não pode criar um clan com este nome.",
            CLAN_NAME_INVALIDO2 = "§cO Nome do clan deve possuir entre 5 a 20 caracteres.",
            CLAN_EXIST = "§cEste clan já existe, escolha outro nome.",
            CLAN_TAG_INVALIDA = "§cA tag do clan deve possuir entre 3 a 5 caracteres.",
            VOCE_NAO_PODE_DELETAR = "§cApenas o §4§lDONO §cdo clan pode deletar o clan.",
            CLAN_DESFEITO = "§fO seu clan foi deletado.",
            VOCE_NAO_PODE_SAIR = "§cVocê não pode sair do clan!",
            VOCE_SAIU_DO_CLAN = "§cVocê saiu do clan!",
            PLAYER_SAIU_DO_CLAN = "§c%nick% §csaiu do clan!",
            PLAYER_É_O_NOVO_DONO = "§a%nick% §aé o novo dono do clan!",
            VOCE_NAO_PODE_EXPULSAR = "§cVocê não pode expulsar alguém do clan.",
            JOGADOR_NAO_ESTA_NO_SEU_CLAN = "§cEste jogador não está no seu clan.",
            JOGADOR_EXPULSO_CLAN = "§aJogador expulso do clan!",
            VOCE_NAO_PODE_SE_CONVIDAR = "§cVocê não pode convidar a sí mesmo.",
            VOCE_NAO_PODE_CONVIDAR = "§cVocê não possui permissão para convidar alguém para o clan.",
            JA_TEM_CONVITE = "§cO jogador já possui um convite.",
            VOCE_ENTROU_NO_CLAN = "§aVocê entrou no clan!",
            VOCE_NAO_TEM_CONVITE_PENDENTE = "§cVocê não possui um convite de um clan.",
            VOCE_NAO_PODE_SE_EXPULSAR = "§cVocê não pode expulsar a sí mesmo.",
            VOCE_NAO_PODE_EXPULSAR_O_DONO = "§cVocê não pode expulsar o §4§lDONO §cdo clan.",
            VOCE_FOI_EXPULSO_DO_CLAN = "§cVocê foi expulso do clan!",
            PLAYER_EXPULSO_DO_CLAN = "§c%nick% §cfoi expulso do clan!",
            PLAYER_ENTROU_NO_CLAN = "§a%nick% §ajuntou-se ao clan!",
            JOGADOR_JA_ESTA_EM_UM_CLAN = "§cO jogador já possui um clan.",
            VOCE_CONVIDOU = "§aVocê convidou %nick% para o clan",
            VOCE_RECEBEU_CONVITE = "§eVocê recebeu um convite para juntar-se ao clan: %clan%\n" +
                    "§cUtilize: /clan aceitar para entrar!",
            APENAS_O_DONO_PODE_PROMOVER = "§cApenas o §4§lDONO §cdo clan pode promover alguém.",
            VOCE_NAO_PODE_SE_REBAIXAR = "§cVocê não pode rebaixar a sí mesmo.",
            VOCE_NAO_PODE_SE_PROMOVER = "§cVocê não pode promover a sí mesmo.",
            JA_ESTA_PROMOVIDO = "§cEste jogador já é um administrador §cdo clan.",
            VOCE_FOI_PROMOVIDO = "§aVocê foi promovido para administração do clan.",
            VOCE_PROMOVEU = "§aVocê promoveu %nick% para administrador do clan.",
            VOCE_REBAIXOU = "§cVocê rebaixou %nick% para membro do clan.",
            APENAS_O_DONO_PODE_REBAIXAR = "§fApenas o §4§lDONO §fdo clan pode promover alguém.",
            NAO_ESTA_PROMOVIDO = "§cEste jogador não é um administrador do clan.",
            VOCE_FOI_REBAIXADO = "§cVocê foi rebaixado para membro do clan.",
            VOCE_NAO_TEM_COINS_O_SUFICIENTE_PARA_CRIAR_CLAN = "§cVocê precisa de mais %coins% §cpara criar um clan.";
}
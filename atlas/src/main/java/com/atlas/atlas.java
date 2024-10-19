package com.atlas;
import java.util.*;
import java.io.FileInputStream;
// import java.io.IOException;
import java.io.InputStream;
// import java.text.*;
import org.yaml.snakeyaml.Yaml;
import org.json.JSONObject;

public class atlas {
    static String atlasASCII = 
        "    ___   __  __           \n" +
        "   /   | / /_/ /___ ______ \n" +
        "  / /| |/ __/ / __ `/ ___/ \n" +
        " / ___ / /_/ / /_/ (__  )   \n" +
        "/_/  |_\\__/_/\\__,_/____/    \n";

    static String menuOptions = 
        "\u001B[1;36m[1]\u001B[0m delete channels   \u001B[1;36m[2]\u001B[0m create channels   \n" +
        "\u001B[1;36m[3]\u001B[0m delete roles      \u001B[1;36m[4]\u001B[0m create roles   \n" +
        "\u001B[1;36m[5]\u001B[0m spam webhooks     \u001B[1;36m[6]\u001B[0m create webhooks   \n" +
        "\u001B[1;36m[7]\u001B[0m re-name guild     \u001B[1;36m[8]\u001B[0m exit   \n";

    static String templateinformation = 
        "logged as \u001B[1;36m{1}\u001B[0m | discord: \u001B[1;36mdsc.gg/plutoserver\u001B[0m      \n" +
        "made by \u001B[1;36m@techplayz32\u001B[0m              | github: \u001B[1;36mgithub.com/techplayz32\u001B[0m   \n";

    static String information = null;

    public static void printMenu() {
        atlasUtils.printColorfulText(atlasASCII, 6);

        System.out.println(information);
        System.out.println(menuOptions);
    }

    static String baseUrl = null;
    static String guildID = null;
    static Map<String, String> globalHeaders = null;

    @SuppressWarnings("unchecked")
    public static String checkIfTokenIsValid() throws Exception {
        Yaml yaml = new Yaml();
        String responseBody = null;
        String workingDir = System.getProperty("user.dir");

        try (InputStream inputStream = new FileInputStream(workingDir + "/config.yaml")) {
            Map<String, Object> data = yaml.load(inputStream);
            Map<String, Object> bot = (Map<String, Object>) data.get("bot");
            Map<String, Object> other = (Map<String, Object>) data.get("other");

            String token = (String) bot.get("token");
            String url = (String) other.get("url");
            String apiVersion = (String) other.get("api");
            Object guildIDsObj = bot.get("guildID");

            if (guildIDsObj instanceof String) {
                guildID = (String) guildIDsObj;
            } else if (guildIDsObj instanceof Long) {
                guildID = String.valueOf(guildIDsObj);
            } else {
                throw new Exception("Invalid type for guildID in config.yaml");
            }

            baseUrl = url + apiVersion;

            Map<String, String> headers = Map.of(
                "Authorization", "Bot " + token,
                // "X-Forwarded-For", "%00, %0d%0a, %09, %0C, %20, %0",
                "X-Forwarded-For", "127.0.0.1",
                // "X-Forwarded-By", "127.0.0.1",
                // "X-Forwarded-For", "127.0.0.1",
                "X-Forwarded-For-Original", "127.0.0.1",
                "X-Forwarder-For", "127.0.0.1",
                "X-Forward-For", "127.0.0.1",
                "Forwarded-For", "127.0.0.1",
                "Forwarded-For-Ip", "127.0.0.1",
                //"X-Custom-IP-Authorization", "127.0.0.1"//,
                "X-Originating-IP", "127.0.0.1",
                "X-Remote-IP", "127.0.0.1",
                "X-Remote-Addr", "127.0.0.1"
            );

            globalHeaders = headers;

            responseBody = atlasUtils.sendGetRequest(baseUrl + "/applications/@me", headers);

            if (responseBody != null) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONObject botObject = jsonResponse.getJSONObject("bot");
                String username = botObject.getString("username");
                String discriminator = botObject.getString("discriminator");

                String fullUsername = username + "#" + discriminator;
                information = templateinformation.replace("{1}", fullUsername);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return responseBody;
    }

    public static void printEverything() {
        printMenu();

        Scanner input = new Scanner(System.in);
        System.out.print("choice: \u001B[1;36m");
        int choice = 0;

        // https://stackoverflow.com/a/13102066
        try {
            choice = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        System.out.println("\u001B[0m");

        switch (choice) {
            case 1:
                atlasFunctions.deleteChannels();
                atlasUtils.clearConsole();
                printEverything();
                break;

            case 2:
                String channelsName = null;
                int amountOfChannels = 0;

                System.out.print("\u001B[0mchannels' name: \u001B[1;36m");
                channelsName = input.nextLine();

                System.out.print("\u001B[0mamount: \u001B[1;36m");
                try {
                    amountOfChannels = Integer.parseInt(input.nextLine());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                atlasFunctions.createChannels(channelsName, amountOfChannels);
                atlasUtils.clearConsole();
                printEverything();
                break;

            case 3:
                atlasFunctions.deleteRoles();
                atlasUtils.clearConsole();
                printEverything();
                break;

            case 4:
                String rolesName = null;
                int amountOfRoles = 0;

                System.out.print("\u001B[0mroles' name: \u001B[1;36m");
                rolesName = input.nextLine();

                System.out.print("\u001B[0mamount: \u001B[1;36m");
                try {
                    amountOfRoles = Integer.parseInt(input.nextLine());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                atlasFunctions.createRoles(rolesName, amountOfRoles);
                atlasUtils.clearConsole();
                printEverything();
                break;

            case 5:
                String context = null;
                System.out.print("\u001B[0mcontext of message: \u001B[1;36m");

                context = input.nextLine();
                System.out.println("\n");

                atlasUtils.printColorfulText("WARNING: if you want to end spamming, please press CTRL+C", 3);
                atlasFunctions.spamWebhooks(context);
                // atlasUtils.clearConsole();
                printEverything();
                break;

            case 6:
                String webhooksName = null;
                System.out.print("\u001B[0mwebhooks' name: \u001B[1;36m");
                webhooksName = input.nextLine();

                atlasFunctions.createWebhooks(webhooksName);
                atlasUtils.clearConsole();
                printEverything();
                break;
                

            case 7:

                break;

            case 8:
                System.out.println("exiting the program..");
                input.close();
                System.exit(0);
                break;

            default:
                atlasUtils.printError("invalid choice!");
                atlasUtils.clearConsole();
                printEverything();
                break;
        }
        printEverything();
    }

    public static void main(String[] args) {
        atlasUtils.clearConsole();

        try {
            checkIfTokenIsValid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++) {
            atlasUtils.printColorfulText(atlasASCII, 6);
            atlasUtils.printLoadingAnimation("loading \u001B[1;36mAtlas\u001B[0m", 3, 500);
            atlasUtils.clearConsole();
        }

        printEverything();
    }
}
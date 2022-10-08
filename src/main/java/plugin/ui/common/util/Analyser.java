package plugin.ui.common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Analyser {

    private static final String[][] TRANSLATIONS = {
            {"jcas"},
            {"jlen", "jhea", "", "", "jbg", "jmax"},
            {"enemyIndex", "number", "start/2", "respawn1", "respawn2", "BHPercent",
                    "layer1", "layer2", "isBoss", "multiple"}
    };

    public static void read_csv_file(File file, BiConsumer<String, Integer> doSth) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), StandardCharsets.UTF_8));

            int i = 0;
            int temp;
            String line;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {

                if ((temp = line.indexOf("/")) != -1)
                    line = line.substring(0, temp);
                if (!line.isEmpty()) {
                    line = trimEmpty(line);
                    doSth.accept(line, i);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, Object> getStageBaseAndEnemyData(File file) {
        Map<String, Object> stageData = new HashMap<>();
        read_csv_file(file, (line, index) -> lineIntoMap(index, line.split(","), stageData));
        return processData(stageData);
    }

    public static Map<Integer, int[]> getStageMusicData(File file) {
        // 1 -> [33, 100, 33]
        Map<Integer, int[]> musicData = new HashMap<>();
        final int useLine = 2;
        read_csv_file(file, (line, index) -> {
            if (index >= useLine) {
                int[] array = Arrays.stream(line.split(",")).skip(2).limit(3).mapToInt(Integer::parseInt).toArray();
                musicData.put(index - useLine, array);
            }
        });
        return musicData;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> processData(Map<String, Object> stageData) {
        ArrayList<List<Integer>> enemy = (ArrayList<List<Integer>>) stageData.get("enemy");
        if (enemy != null) {
            enemy.forEach((list -> {
                for (int i = 2; i <= 4; i++) {
                    list.set(i, list.get(i) * 2);
                }
            }));
        }
        return stageData;
    }

    @SuppressWarnings("unchecked")
    private static void lineIntoMap(int index, String[] rawData, Map<String, Object> stageData) {
        String[] lineTranslations = TRANSLATIONS[index >= TRANSLATIONS.length ? TRANSLATIONS.length - 1 : index];
        int length = lineTranslations.length;
        if (length > rawData.length) {
            throw new UIException("mismatching data, line=" + index);
        }

        if (index <= 1) {
            // stage basic data
            for (int i = 0; i < length; i++) {
                String key = lineTranslations[i];
                if (!key.isEmpty()) {
                    stageData.put(key, rawData[i]);
                }
            }
        } else {
            // stage enemy data
            if ("0".equals(rawData[0])) return; // useless enemy
            ArrayList<List<Integer>> enemy = (ArrayList<List<Integer>>) stageData.get("enemy");
            if (enemy == null) {
                enemy = new ArrayList<>();
                stageData.put("enemy", enemy);
            }

            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                list.add(Integer.parseInt(rawData[i]));
            }

            enemy.add(list);
        }
    }

    public static String trimEmpty(String input) {
        String regx = "\\s*|\t|\r";
        return trim(input, regx);
    }

    public static String trim(String input, String regx) {
        Pattern pat = Pattern.compile(regx);
        Matcher m = pat.matcher(input);
        return m.replaceAll("");
    }

    public static int[] ArrayIntegerToInt(List<Integer> array, int skipNum, int limitNum) {
        return array.stream().skip(skipNum).limit(limitNum).mapToInt(Integer::valueOf).toArray();
    }

    public static int[] ArrayIntegerToInt(List<Integer> array, int skipNum) {
        return ArrayIntegerToInt(array, skipNum, array.size());
    }

    public static <T> T[] toReverseArrays(List<T> list, T[] datas) {
        Collections.reverse(list);
        return list.toArray(datas);
    }

    public static int parseInt(String raw, int defaultValue) {
        String value = trim(raw, "[^0-9]");
        return value.isEmpty() ? defaultValue : Integer.parseInt(value);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?\\d*$");
        return pattern.matcher(str).matches();
    }

}

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagAdder {
    private static final Pattern struct_pattern = Pattern.compile("(type\\s+\\w+\\s+struct\\s*\\{\\n*)([^}]*)(\\s*})");
    private static final Pattern field_pattern = Pattern.compile("([ \t]*(\\w+)\\s+([*.a-zA-Z0-9\\[\\]]+))(\\s+(`[^{`]+`))?\\s*\n");

    public static String ModifyTag(String content, boolean json, boolean bson) {

        // replace
        Matcher struct_matcher = struct_pattern.matcher(content);
        StringBuffer modified = new StringBuffer();
        while (struct_matcher.find()) {
            //System.out.println(struct_matcher.start());
            //System.out.println(struct_matcher.end());
            //System.out.println(struct_matcher.group(2));
            // get max length
            Matcher field_matcher = field_pattern.matcher(struct_matcher.group(0));
            int max_len = 0;
            while (field_matcher.find()) {
                int len = field_matcher.group(1).length();
                if (len > max_len) {
                    max_len = len;
                }
            }
            field_matcher = field_pattern.matcher(struct_matcher.group(0));

            StringBuffer modified_fields = new StringBuffer();
            while (field_matcher.find()) {
                String tag_name = field_matcher.group(2);
                tag_name = Character.toLowerCase(tag_name.charAt(0)) + tag_name.substring(1);
                String bson_tag = String.format("bson:\"%s\"", tag_name);
                String json_tag = String.format("json:\"%s\"", tag_name);
                if (tag_name.equals("id")) {
                    bson_tag = String.format("bson:\"%s\"", "_id");
                }
                String tag = "";
                if (bson) {
                    tag = bson_tag;
                }
                if (json) {
                    if (!tag.equals("")) {
                        tag += " ";
                    }
                    tag += json_tag;
                }
                StringBuffer spaces = new StringBuffer();
                for (int i = 0; i < max_len - field_matcher.group(1).length(); i++) {
                    spaces.append(" ");
                }
                String field = String.format("%s %s`%s`\n", field_matcher.group(1),spaces, tag);
                field_matcher.appendReplacement(modified_fields, field);
            }
            field_matcher.appendTail(modified_fields);
            struct_matcher.appendReplacement(modified, modified_fields.toString());
        }
        struct_matcher.appendTail(modified);
        return modified.toString();
    }

    public static void test(String[] args) {
        final String context = "type IapLocation struct {\n" +
                "\tName        string\n" +
                "\tDescription string\n" +
                "}\n" +
                "type AppEdit struct {\n" +
                "\tItem\n" +
                "\tSlug              []string                       `json:\"slug\"`\n" +
                "\tCategories        []string                       `json:\"categories\"`\n" +
                "\tPackageName       string                         `json:\"packageName\"`\n" +
                "\tName              string                         `json:\"name\"`\n" +
                "\tClientId          string                         `json:\"clientId\"`\n" +
                "\tImage             *AppImages                     `json:\"image\"`\n" +
                "\tDefaultLanguage   string                         `json:\"defaultLanguage\"`\n" +
                "\tDescription       string                         `json:\"description\"`\n" +
                "\tMinVersionSupport string                         `json:\"minVersionSupport\"`\n" +
                "\tPlatform          string                         `json:\"platform\"`\n" +
                "\tPublishNotes      string                         `json:\"publishNotes\"`\n" +
                "\tSource            string                         `json:\"source\"`\n" +
                "\tVersionName       string                         `json:\"versionName\"`\n" +
                "\tVersionCode       string                         `json:\"versionCode\"`\n" +
                "\tWhatsNew          string                         `json:\"whatsNew\"`\n" +
                "\tBinaries          *AppBinary                     `json:\"binaries\"`\n" +
                "\tStore             map[string]*AppStoreProperties `json:\"store\"`\n" +
                "}\n" +
                "type Item struct {\n" +
                "\tId          string     `json:\"id\"`\n" +
                "\tCreateBy    string     `json:\"createBy\"`\n" +
                "\tCreateTime  *time.Time `json:\"createTime\"`\n" +
                "\tUpdateBy    string     `json:\"updateBy\"`\n" +
                "\tUpdatedTime *time.Time `json:\"updatedTime\"`\n" +
                "\tItemId      string     `json:\"itemId\"`\n" +
                "\tType        string     `json:\"type\"`\n" +
                "\tStatus      string     `json:\"status\"`\n" +
                "}\n" +
                "aaa";
        System.out.println(ModifyTag(context, true, true));
        System.out.println(ModifyTag(context, true, false));
        System.out.println(ModifyTag(context, false, true));
    }
}

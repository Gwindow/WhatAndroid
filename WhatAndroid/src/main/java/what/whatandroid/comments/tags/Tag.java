package what.whatandroid.comments.tags;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tracks the start and end of tag along with the tag name and parameter
 * if provided one. The tag length is the length of the opening tag and
 * accounts for the opening/closing brackets
 */
public class Tag {
    public static final Pattern TAG_PARAM = Pattern.compile("([^\\]/=]+)=([^\\]]+)");
    public int start, end, tagLength;
    public String tag, param;

    /**
     * Create a tag starting at some point
     *
     * @param start location of the opening tags opening bracket
     * @param tag   text between the opening and closing brackets of the opening tag
     */
    public Tag(int start, String tag) {
        this.start = start;
        this.tag = tag;
        this.end = -1;
        tagLength = tag.length() + 2;
        Matcher tagParam = TAG_PARAM.matcher(tag);
        if (tagParam.find()) {
            this.tag = tagParam.group(1);
            param = tagParam.group(2);
        }
    }

    @Override
    public String toString() {
        return "Tag [tag = " + tag + ", param = " + param + ", start = " + start
                + ", end = " + end + ", tagLength = " + tagLength + "]";
    }
}

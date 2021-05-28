package org.rand.aio.io;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AioAntPathResolver {
    /**
     *  getAntPathStringMatcher()源码摘自springboot的AntPathMatcher>AntPathStringMatcher方法
     */
    private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?}|[^/{}]|\\\\[{}])+?)}");

    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    public static Pattern getAntPathStringMatcher(String pattern, boolean caseSensitive) {
        StringBuilder patternBuilder = new StringBuilder();
        Matcher matcher = GLOB_PATTERN.matcher(pattern);
        int end = 0;
        while (matcher.find()) {
            patternBuilder.append(quote(pattern, end, matcher.start()));
            String match = matcher.group();
            if ("?".equals(match)) {
                patternBuilder.append('.');
            }
            else if ("*".equals(match)) {
                patternBuilder.append(".*");
            }
            else if (match.startsWith("{") && match.endsWith("}")) {
                int colonIdx = match.indexOf(':');
                if (colonIdx == -1) {
                    patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                }
                else {
                    String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                    patternBuilder.append('(');
                    patternBuilder.append(variablePattern);
                    patternBuilder.append(')');
                }
            }
            end = matcher.end();
        }
        patternBuilder.append(quote(pattern, end, pattern.length()));
        Pattern compile=(caseSensitive ? Pattern.compile(patternBuilder.toString()) :
                Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE));
        return compile;
    }

    private static String quote(String s, int start, int end) {
        if (start == end) {
            return "";
        }
        return Pattern.quote(s.substring(start, end));
    }

    public static boolean matching(String str,String pattern){
        Pattern antPathStringMatcher = getAntPathStringMatcher(pattern, true);
        return antPathStringMatcher.matcher(str).matches();
    }
    public static boolean doMatch(String path,String pattern){
        return  doMatch(path,pattern,true);
    }
    public static boolean doMatch(String path,String pattern,boolean fullMatch){
        String[] patterns = separator(pattern, "/");
        String[] paths = separator(path, "/");
        int patternLength = patterns.length-1;
        int pathLength=paths.length-1;
        int patternIndex=0;
        int pathIndex=0;
        while (pathIndex<=pathLength&&patternIndex<=patternLength){
            String patt=patterns[patternIndex];
            if("**".equals(patt)){
                break;
            }
            if(!matching(paths[pathIndex],patt)){
                return false;
            }
            pathIndex++;
            patternIndex++;
        }
        // 当表达式(pattern):/a/b/c/*，匹配路径(path):/a/b/c/d.txt 时 则pathIndex>pathLength&&patternIndex>patternLength
        // 当表达式(pattern):/a/b/c/*，匹配路径(path):/a/b/c/ 时 则pathIndex>pathLength&&patternIndex==patternLength
        // 当表达式(pattern):/a/b/c/**，匹配路径(path):/a/b/c/ 时 则pathIndex>pathLength&&patternIndex<patternLength
        if (pathIndex > pathLength) {
            // 结尾必须同是斜杠结尾（/）或者不是
            if (patternIndex > patternLength) {
                return (pattern.endsWith("/") == path.endsWith("/"));
            }
            // 当表达式数组最后一个为*,并且匹配路径字符串结尾必须是/才可能是true
            if (patternIndex == patternLength && patterns[patternIndex].equals("*") && path.endsWith("/")) {
                return true;
            }
            // 表达式数组长度=路径数组长度+1，并且表达式结尾为**
            for (int i = patternIndex; i <= patternLength; i++) {
                if (!patterns[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }
        else if (patternIndex > pathLength) {
            // String not exhausted, but pattern is. Failure.
            return false;
        }
        else if ( !fullMatch&&"**".equals(patterns[patternIndex])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }
        while(pathIndex<=pathLength&&patternIndex<=patternLength){
            String patt=patterns[patternLength];
            if("**".equals(patt)){
                break;
            }
            if(!matching(paths[pathLength],patt)){
                return false;
            }
            pathLength--;
            patternLength--;
        }

        if (pathIndex > pathLength) {
            // String is exhausted
            for (int i = pathIndex; i <= pathLength; i++) {
                if (!patterns[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (patternIndex != patternLength && pathIndex <= pathLength) {
            int patIdxTmp = -1;
            for (int i = patternIndex + 1; i <= patternLength; i++) {
                if (patterns[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patternIndex + 1) {
                // '**/**' situation, so skip one
                patternIndex++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patternIndex - 1);
            int strLength = (pathLength - pathIndex + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = patterns[patternIndex + j + 1];
                    String subStr = paths[pathIndex + i + j];
                    if (!matching(subStr,subPat)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIndex + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patternIndex = patIdxTmp;
            pathIndex = foundIdx + patLength;
        }

        for (int i = patternIndex; i <= patternLength; i++) {
            if (!patterns[i].equals("**")) {
                return false;
            }
        }
        return true;
    }

    static String[] separator(String str,String separator){
        StringTokenizer stringTokenizer = new StringTokenizer(str, separator);
        ArrayList<String> arrayList = new ArrayList<>();
        while(stringTokenizer.hasMoreElements()){
            arrayList.add(stringTokenizer.nextToken());
        }
        return arrayList.toArray(new String[0]);
    }

    protected static String determineRootDir(String location) {
        int prefixEnd = location.indexOf(':') + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }
    public static boolean isPattern(String path) {
        boolean uriVar = false;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '*' || c == '?') {
                return true;
            }
            if (c == '{') {
                uriVar = true;
                continue;
            }
            if (c == '}' && uriVar) {
                return true;
            }
        }
        return false;
    }
}

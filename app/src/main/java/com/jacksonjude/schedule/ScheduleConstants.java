package com.jacksonjude.schedule;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jackson on 3/2/18.
 */

public class ScheduleConstants {
    public final static String cloudKitAPIToken = "7c20ce5c01e766531e354e5a2eb5523c47d76aac13d620c31990d5bda274625d";
    public final static String cloudKitIdentifier = "iCloud.com.jacksonjude.BellSchedule";

    public final static String scheduleRecordName = "Schedule";
    public final static String weekSchedulesRecordName = "WeekSchedules";
    public final static String userScheduleRecordName = "UserSchedule";

    public final static int periodsInFullDay = 8;

    public final static ArrayList<String> MODEL_TYPES = new ArrayList<String>(Arrays.asList(scheduleRecordName, weekSchedulesRecordName, userScheduleRecordName));

    public enum MODEL_TYPES_ENUM
    {
        Schedule("Schedule"),
        WeekSchedules("WeekSchedules");

        private final String modelTypeString;

        MODEL_TYPES_ENUM(final String modelTypeString) {
            this.modelTypeString = modelTypeString;
        }

        @Override
        public String toString() {
            return modelTypeString;
        }
    }

    public enum PERIOD_TYPE
    {
        PERIOD("PERIOD"),
        PASSING_PERIOD("PASSING_PERIOD"),
        BEFORE_SCHOOL("BEFORE_SCHOOL"),
        AFTER_SCHOOL("AFTER_SCHOOL"),
        NO_SCHOOL("NO_SCHOOL"),
        UNKNOWN("UNKNOWN");

        private final String periodTypeString;

        PERIOD_TYPE(final String periodTypeString) {
            this.periodTypeString = periodTypeString;
        }

        @Override
        public String toString() {
            return periodTypeString;
        }
    }

    public enum SCHOOL_TIMES_STATUS
    {
        SCHOOL_TIMES("SCHOOL_TIMES"),
        NO_SCHOOL("NO_SCHOOL"),
        UNKNOWN("UNKNOWN");

        private final String schoolTimesStatusString;

        SCHOOL_TIMES_STATUS(final String schoolTimesStatusString) {
            this.schoolTimesStatusString = schoolTimesStatusString;
        }

        @Override
        public String toString() {
            return schoolTimesStatusString;
        }
    }

    public enum TIME_COMPARISON
    {
        BEFORE("BEFORE"),
        AFTER("AFTER"),
        EQUAL("EQUAL"),
        UNKNOWN("UNKNOWN");

        private final String timeComparisonString;

        TIME_COMPARISON(final String timeComparisonString) {
            this.timeComparisonString = timeComparisonString;
        }

        @Override
        public String toString() {
            return timeComparisonString;
        }
    }
}

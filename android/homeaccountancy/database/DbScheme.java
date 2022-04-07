package ru.nedovizin.homeaccountancy.database;

public class DbScheme {
    public static final class CategoryTable {
        public static final String NAME = "category";
        public static final class Cols {
            public static final String ID = "id";
            public static final String PRIORITY = "priority";
            public static final String PERIOD = "period";
            public static final String TYPE = "type";
            public static final String COLOR = "color";
            public static final String CATEGORY_NAME = "category_name";
            public static final String EXPOSED = "exposed";
            public static final String RESERVED = "reserved";
            public static final String PLANNED = "planned";
        }
    }

    public static final class CategoryNameTable {
        public static final String NAME = "category_name";
        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String TYPE = "type";
        }
    }

    public static final class OperationTable {
        public static final String NAME = "operation";
        public static final class Cols {
            public static final String ID = "id";
            public static final String VALUE = "value";
            public static final String CATEGORY = "category";
            public static final String PERIOD = "period";
        }
    }
}

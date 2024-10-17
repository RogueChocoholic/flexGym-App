package model;

public enum Validation {
    NAME {

        @Override
        public String validation() {
            return "/^[\\p{L} ,.'-]+$/u";
        }
    }, PASSWORD {
        @Override
        public String validation() {
            return "/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,32}$/";
        }

    }, MOBILE {
        @Override
        public String validation() {
            return "^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$";
        }

    }, EMAIL {
        @Override
        public String validation() {
            return "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";
        }

    }, NIC {
        @Override
        public String validation() {
            return "^(?:19|20)?\\d{2}(?:[0-35-8]\\d\\d(?<!(?:000|500|36[7-9]|3[7-9]\\d|86[7-9]|8[7-9]\\d)))\\d{4}(?i:v|x)$";
        }

    };

    public String validation() {
        return "";
    }
}

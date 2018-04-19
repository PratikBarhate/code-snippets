import tensorflow as tf


def read_data(data_type: str):
    # define all the constants
    train_data_file = "../dataset/adult_train.csv"
    test_data_file = "../dataset/adult_test.csv"
    _CSV_COLUMNS = [
        'age', 'workclass', 'fnlwgt', 'education', 'education_num',
        'marital_status', 'occupation', 'relationship', 'race', 'gender',
        'capital_gain', 'capital_loss', 'hours_per_week', 'native_country',
        'income_bracket'
    ]
    _CSV_COLUMN_DEFAULTS = [[0], [""], [0], [""], [0], [""], [""], [""], [""], [""],
                            [0], [0], [0], [""], [""]]
    _NUM_EXAMPLES = {
        "train": 32561,
        "validation": 16281,
    }

    # start processing
    data_file, process = (train_data_file, "train") if data_type == "training" else (test_data_file, "validation")

    def parse_csv(value):
        print("Parsing {0} data.".format(data_type))
        columns = tf.decode_csv(value, record_defaults=_CSV_COLUMN_DEFAULTS)
        features = dict(zip(_CSV_COLUMNS, columns))
        labels = features.pop("income_bracket")
        return features, tf.equal(labels, ">50K")

    dataset = tf.data.TextLineDataset(data_type)
    dataset = dataset.shuffle(buffer_size=_NUM_EXAMPLES[process])
    dataset = dataset.map(parse_csv, num_parallel_calls=5)
    dataset = dataset.repeat(count=3)
    dataset = dataset.batch(batch_size=100)
    return dataset


def build_columns():
    # Continuous columns
    age = tf.feature_column.numeric_column('age')
    education_num = tf.feature_column.numeric_column('education_num')
    capital_gain = tf.feature_column.numeric_column('capital_gain')
    capital_loss = tf.feature_column.numeric_column('capital_loss')
    hours_per_week = tf.feature_column.numeric_column('hours_per_week')

    # categorical columns
    education = tf.feature_column

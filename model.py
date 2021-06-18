from ibm_watson_machine_learning import APIClient
import json
import numpy as np
import pandas as pd

from config import *


wml_credentials = {"apikey": IBM_CONNECT_API_KEY, "url": IBM_URL}
wml_client = APIClient(wml_credentials)
deployment_uid = wml_client.deployments.get_uid(ML_DEPLOYMENT)


def get_model_prediction(vehicle_properties):
    """
    :param vehicle_properties:
    :return:
    """
    x_test = pd.read_json(vehicle_properties)
    payload = {"input_data": [{"fields": x_test.columns.to_numpy().tolist(),
                               "values": x_test.to_numpy().tolist()}]}

    result = wml_client.deployments.score(deployment_uid, payload)
    return result['predictions'][0]["values"][0][0]


export const getFilestoreId = () => {
  const origin = window.location.origin;
  if (origin.includes("kerala-dev")) {
    return "2aefb901-edc6-4a45-95f8-3ea383a513f5";
  } else if (origin.includes("kerala-qa")) {
    return "0cdd01bf-5c6c-43de-86df-48406ce4f5a8";
  } else if (origin.includes("kerala-hc")) {
    return "efc91171-a5ed-4849-acfb-96c58627ecd3";
  } else if (origin.includes("oncourts-uat")) {
    return "efc91171-a5ed-4849-acfb-96c58627ecd3";
  } else {
    return "efc91171-a5ed-4849-acfb-96c58627ecd3";
  }
};

#include <iostream>

#define HASH_CHAR '#'

std::string &convert(const std::string &str_in) {
  int len = str_in.size();
  int conv_len = 2 * len + 1;
  static std::string conv_str(conv_len, HASH_CHAR);
  for (int i = 0; i < len; i++) conv_str[2 * i + 1] = str_in[i];
  return conv_str;
}

int main() {
  std::string s1 = "test";
  std::string s2 = "xy";
  std::string c1 = convert(s1);
  std::cout << "First string: " << c1 << std::endl;
  std::string c2 = convert(s2);
  std::cout << "First AGAIN: " << c1 << std::endl;
  std::cout << "Second string: " << c2 << std::endl;
}
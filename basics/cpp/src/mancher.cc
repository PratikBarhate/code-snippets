#include <iostream>
#include <vector>

#define HASH_CHAR '#'

/**
 * Return a new string with '#' in between
 * each letter as well as padding the string.
 * e.g abc -> #a#b#c#
 */
std::string convert(const std::string &in_str) {
  int len = in_str.size();
  int conv_len = 2 * len + 1;
  std::string conv_str(conv_len, HASH_CHAR);
  for (int i = 0; i < len; i++) conv_str[2 * i + 1] = in_str[i];
  return conv_str;
}

/**
 * Longest palindrome using matrix of n x n;
 * where n = length of the given string.
 */
std::vector<std::vector<int>> longest_pal(const std::string &in_str) {
  int len = in_str.size();
  std::vector<std::vector<int>> pal_mat(len, std::vector<int>(len, 0));
  for (int i = 0; i < len; i++) pal_mat[i][i] = 1;
}

std::vector<int> manacher_algo(const std::string &conv_str) {
  int len = conv_str.size();
  std::vector<int> pal_len_arr;
  pal_len_arr.push_back(0);
  return pal_len_arr;
}

void main() {
  std::cout << "Input string: ";
  std::string input;
  std::cin >> input;
  std::string conv_str = convert(input);
}
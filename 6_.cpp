#include <string>
#include <vector>
#include <iostream>
using namespace std;

int map[31][31];
int pattern[3][2] = { { 1,1 },{ 1,0 },{ 0,1 } };
int visited[30][30];
int height[30];
int M, N;
int ok_(int x, int y) {

	int target = map[x][y];

	if (target == 0) return 1;

	for (int i = 0; i < 3; i++) {
		if (target != map[x + pattern[i][0]][y + pattern[i][1]])
			return 1;
	}
	visited[x][y] = 1;
	for (int i = 0; i < 3; i++) {
		visited[x + pattern[i][0]][y + pattern[i][1]] = 1;
	}
	return 0;
}

void init_visited() {
	for (int i = 0; i < 30; i++)
		for (int j = 0; j < 30; j++)
			visited[i][j] = 0;

}
int bomb_junhee() {
	int temp = 0;
	for (int i = 0; i < M; i++) {
		for (int j = 0; j < N; j++) {
			if (visited[i][j] == 1)
			{
				map[i][j] = 0;
				temp++;
			}
		}
	}
	return temp;
}

void combine() {
	for (int j = 0; j < N; j++) {
		for (int i = M - 1; i >= 0; i--) {
			if (map[i][j] == 0) {
				for (int k = i; k < M; k++)
					map[k][j] = map[k + 1][j];
			}
		}
	}
}


int solution(int m, int n, vector<string> board) {
	int answer = 0;
	M = m;
	N = n;
	for (int i = 0; i < 31; i++)
		for (int j = 0; j < 31; j++) map[i][j] = 0;

	for (int i = 0; i < 30; i++)
		height[i] = board.size();

	for (int i = 0; i < m; i++) {
		for (int j = 0; j < n; j++) {
			map[m - 1 - i][n - 1 - j] = board[i].at(j);
		}
	}

	int flag = 0;
	do {
		flag = 0;
		for (int i = 0; i < m - 1; i++) {
			for (int j = 0; j < n; j++) {
				if (i == m - 2 && j == n - 1 || map[i][j] == 0) {

				}
				else {
					if (ok_(i, j) == 0)
						flag = 1;
				}
			}

		}

		if (flag == 1) {
			answer += bomb();
			combine();
		}
		init_visited();
	} while (flag == 1);


	return answer;
}

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _isLogin = true;
  String _email = '';
  String _password = '';
  String _confirmPassword = '';
  String _name = '';
  String _phone = '';
  bool _isLoading = false; // Add loading state

  String getApiBaseUrl() {
    if (kIsWeb) {
      return "http://localhost:8080"; // 웹에서 실행 시
    } else if (Platform.isAndroid) {
      return "http://10.0.2.2:8080"; // Android 에뮬레이터
    } else {
      return "http://localhost:8080"; // iOS 시뮬레이터 등
    }
  }

  Future<void> _submitAuthForm() async {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();

      if (_password != _confirmPassword && !_isLogin) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('비밀번호가 일치하지 않습니다.')),
        );
        return;
      }

      setState(() {
        _isLoading = true;
      });

      try {
        if (_isLogin) {
          // Login Logic
          final url = Uri.parse('${getApiBaseUrl()}/api/auth/login'); // 환경별 API 주소 사용
          final response = await http.post(
            url,
            headers: {'Content-Type': 'application/json'},
            body: json.encode({
              'email': _email,
              'password': _password,
            }),
          );

          print('Login Response Status Code: ${response.statusCode}');
          print('Login Response Body: ${response.body}');

          if (response.statusCode == 200) {
            final responseData = json.decode(response.body);
            final accessToken = responseData['accessToken'];
            final prefs = await SharedPreferences.getInstance();
            await prefs.setString('accessToken', accessToken);

            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('로그인 성공!')),
            );
            Navigator.of(context).pushReplacementNamed('/home');
          } else {
            final errorData = json.decode(response.body);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(errorData['message'] ?? '로그인 실패: 알 수 없는 오류')),
            );
          }
        } else {
          // Registration Logic
          final url = Uri.parse('${getApiBaseUrl()}/api/auth/register'); // 환경별 API 주소 사용
          final response = await http.post(
            url,
            headers: {'Content-Type': 'application/json'},
            body: json.encode({
              'email': _email,
              'password': _password,
              'name': _name,
              'phone': _phone,
              'role': 'CUSTOMER',
            }),
          );

          print('Register Response Status Code: ${response.statusCode}');
          print('Register Response Body: ${response.body}');

          if (response.statusCode == 200) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('회원가입이 성공적으로 완료되었습니다.')),
            );
            setState(() {
              _isLogin = true; // Switch to login mode after successful registration
            });
          } else {
            final errorData = json.decode(response.body);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(errorData['message'] ?? '회원가입 실패: 알 수 없는 오류')),
            );
          }
        }
      } catch (error) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('오류 발생: $error')),
        );
      } finally {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          _isLogin ? '로그인' : '회원가입',
          style: Theme.of(context).appBarTheme.titleTextStyle, // Apply app bar title style
        ),
        backgroundColor: Theme.of(context).appBarTheme.backgroundColor, // Apply app bar background color
        foregroundColor: Theme.of(context).appBarTheme.foregroundColor, // Apply app bar foreground color
      ),
      body: Center(
        child: Card(
          margin: const EdgeInsets.all(20),
          elevation: Theme.of(context).cardTheme.elevation, // Apply card elevation
          shape: Theme.of(context).cardTheme.shape, // Apply card shape
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Form(
              key: _formKey,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  TextFormField(
                    key: const ValueKey('email'),
                    keyboardType: TextInputType.emailAddress,
                    decoration: InputDecoration(
                      labelText: '이메일 주소',
                      border: Theme.of(context).inputDecorationTheme.border,
                      focusedBorder: Theme.of(context).inputDecorationTheme.focusedBorder,
                      labelStyle: Theme.of(context).inputDecorationTheme.labelStyle,
                    ),
                    validator: (value) {
                      if (value!.isEmpty || !value.contains('@')) {
                        return '유효한 이메일 주소를 입력해주세요.';
                      }
                      return null;
                    },
                    onSaved: (value) {
                      _email = value!;
                    },
                  ),
                  const SizedBox(height: 16), // Increased spacing
                  TextFormField(
                    key: const ValueKey('password'),
                    decoration: InputDecoration(
                      labelText: '비밀번호',
                      border: Theme.of(context).inputDecorationTheme.border,
                      focusedBorder: Theme.of(context).inputDecorationTheme.focusedBorder,
                      labelStyle: Theme.of(context).inputDecorationTheme.labelStyle,
                    ),
                    obscureText: true,
                    validator: (value) {
                      if (value!.isEmpty || value.length < 6) {
                        return '비밀번호는 6자 이상이어야 합니다.';
                      }
                      return null;
                    },
                    onSaved: (value) {
                      _password = value!;
                    },
                  ),
                  if (!_isLogin)
                    TextFormField(
                      key: const ValueKey('name'),
                      decoration: InputDecoration(
                        labelText: '이름',
                        border: Theme.of(context).inputDecorationTheme.border,
                        focusedBorder: Theme.of(context).inputDecorationTheme.focusedBorder,
                        labelStyle: Theme.of(context).inputDecorationTheme.labelStyle,
                      ),
                      validator: (value) {
                        if (value!.isEmpty) {
                          return '이름을 입력해주세요.';
                        }
                        return null;
                      },
                      onSaved: (value) {
                        _name = value!;
                      },
                    ),
                  if (!_isLogin)
                    TextFormField(
                      key: const ValueKey('phone'),
                      keyboardType: TextInputType.phone,
                      decoration: InputDecoration(
                        labelText: '전화번호 (선택 사항)',
                        border: Theme.of(context).inputDecorationTheme.border,
                        focusedBorder: Theme.of(context).inputDecorationTheme.focusedBorder,
                        labelStyle: Theme.of(context).inputDecorationTheme.labelStyle,
                      ),
                      onSaved: (value) {
                        _phone = value!;
                      },
                    ),
                  if (!_isLogin)
                    Padding(
                      padding: const EdgeInsets.only(top: 16.0), // Spacing for confirm password
                      child: TextFormField(
                        key: const ValueKey('confirmPassword'),
                        decoration: InputDecoration(
                          labelText: '비밀번호 확인',
                          border: Theme.of(context).inputDecorationTheme.border,
                          focusedBorder: Theme.of(context).inputDecorationTheme.focusedBorder,
                          labelStyle: Theme.of(context).inputDecorationTheme.labelStyle,
                        ),
                        obscureText: true,
                        validator: (value) {
                          if (value!.isEmpty || value.length < 6) {
                            return '비밀번호는 6자 이상이어야 합니다.';
                          }
                          return null;
                        },
                        onSaved: (value) {
                          _confirmPassword = value!;
                        },
                      ),
                    ),
                  const SizedBox(height: 24), // Increased spacing before button
                  _isLoading
                      ? const CircularProgressIndicator() // Show loading indicator
                      : ElevatedButton(
                          onPressed: _submitAuthForm,
                          style: Theme.of(context).elevatedButtonTheme.style, // Apply elevated button style
                          child: Text(_isLogin ? '로그인' : '회원가입'),
                        ),
                  TextButton(
                    onPressed: _isLoading
                        ? null
                        : () {
                            setState(() {
                              _isLogin = !_isLogin;
                            });
                          },
                    style: TextButton.styleFrom(
                      foregroundColor: Theme.of(context).primaryColor, // Use primary color for text button
                    ),
                    child: Text(_isLogin ? '새 계정 만들기' : '이미 계정이 있습니다.'),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}


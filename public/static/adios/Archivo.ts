import React from 'react';
import { router } from 'expo-router';
import { Text, View, Image, KeyboardAvoidingView, Platform, ScrollView, StatusBar, Alert } from 'react-native';

// Componentes locales
import { Screen } from '../components/Screen';
import ButtonCont from '../components/ButtonCont';

// Recursos de estilos e imágenes
import { colors, fonts } from '../theme/styles';

const AddDataSuggest = () => {
  return (
    <Screen>
      <StatusBar backgroundColor={colors.bluePrimary} barStyle='light-content' />
      <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
        <View style={{ flex: 1 }}>
          <ScrollView contentContainerStyle={{ flexGrow: 1 }} bounces={false}>

            {/* Logo */}
            <View className='flex flex-col items-left mt-12 mx-6'>
              <Image source={require('../assets/logoPenguin.png')} style={{ width: 100, height: 100, marginBottom: -20 }} />
              <Text className='text-2xl' style={fonts.raleway}>
                Hydrasense
              </Text>
            </View>

            {/* Recordatorio */}
            <View className='mx-6' style={{ marginTop: 60 }}>
              <Text className='text-6xl text-right mb-6' style={fonts.ralewayBold}>
                Permítenos conocerte mejor
              </Text>
              <Text className='text-xl text-right ms-8' style={fonts.poppins}>
                Nos encantaría que nos compartieras tu información para poder ofrecerte un monitoreo completamente adaptado a ti, pensado especialmente para tu comodidad y bienestar.
              </Text>
            </View>

            {/* Botones de confirmación */}
            <View className='flex flex-row justify-end gap-2 mt-10 mx-6'>
              <ButtonCont label='Omitir' color={colors.gray} onPress={() => 
                Alert.alert(
                  'Estás a punto de omitir este paso',
                  'Brindar tu información nos ayuda a ofrecerte un mejor servicio. ¿Estás seguro de omitirlo?',
                  [
                    { text: 'Cancelar', onPress: () => console.log('Cancel Pressed'), },
                    { text: 'Aceptar', onPress: () => router.push('HomeRender') }
                  ]
                )
              } />
              <ButtonCont label='Continuar' color={colors.bluePrimary} onPress={() => router.push('EditProfileRender')} />
            </View>
          </ScrollView>

          {/* Imagen de pie de pantalla */}
          <Image source={require('../assets/AddDataSuggest/swirlingWater.jpg')} style={{ width: '100%', height: 150 }} />

        </View>
      </KeyboardAvoidingView>
    </Screen>
  );
};

export default AddDataSuggest;
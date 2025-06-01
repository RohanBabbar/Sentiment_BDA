import streamlit as st
import pandas as pd
import matplotlib.pyplot as plt

st.title("📊 Social Media Sentiment Dashboard")

# Load Data
df = pd.read_csv("final_sentiment_results.csv")

# Pie Chart
st.subheader("Sentiment Distribution")
sentiment_counts = df['sentiment'].value_counts()
fig, ax = plt.subplots()
ax.pie(sentiment_counts, labels=sentiment_counts.index, autopct='%1.1f%%', startangle=90)
ax.axis('equal')
st.pyplot(fig)

# Show Examples
st.subheader("Sample Tweets by Sentiment")
sentiment = st.selectbox("Choose sentiment:", df['sentiment'].unique())
filtered_df = df[df['sentiment'] == sentiment]

if len(filtered_df) >= 5:
    st.write("Available columns:", filtered_df.columns.tolist())
    st.write(filtered_df.sample(5)[['clean_text']])
else:
    
    st.write(filtered_df[['clean_text']])
    st.info(f"Only {len(filtered_df)} posts found for sentiment: {sentiment}")